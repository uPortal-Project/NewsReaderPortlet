#!/usr/bin/env bash
#
# Licensed to Apereo under one or more contributor license
# agreements. See the NOTICE file distributed with this work
# for additional information regarding copyright ownership.
# Apereo licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file
# except in compliance with the License.  You may obtain a
# copy of the License at the following location:
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -euo pipefail

usage() {
  cat <<'EOF'
Usage: release-portlet.sh --release X.Y.Z --next X.Y.Z+1-SNAPSHOT [options]

Required:
  --release VER       version to cut, e.g. 3.4.3
  --next VER          next snapshot, e.g. 3.4.4-SNAPSHOT

Options:
  --branch NAME       branch to release from (default: master)
  --require-pr SHA    require commit SHA reachable from HEAD (repeatable)

Environment:
  OSSRH_USERNAME      Central Portal user token name (required)
  OSSRH_PASSWORD      Central Portal user token password (required)

Phases:
  1. Tree state — clean, on expected branch, in sync with upstream
  2. Env — Java 11, mvn, ~/.m2/settings.xml, GPG key on keys.openpgp.org
  3. POM hygiene — no stale OSSRH URLs, <developers> present
  4. Build smoke — mvn clean install
  5. Styling — mvn notice:generate / license:format / javadoc:fix; aborts on drift
  6. Artifact discovery — groupId/artifactId from POM, build canary URL
  7. Summary + y/N confirmation
  8. Cut release — mvn -B release:clean release:prepare release:perform
  9. OSSRH POST — push staged repo into Central Portal staging UI
 10. Print next steps (Publish in Portal UI, watch propagation)
EOF
  exit 1
}

RELEASE=""; NEXT=""; BRANCH="master"; REQUIRE_PRS=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --release) RELEASE="$2"; shift 2 ;;
    --next) NEXT="$2"; shift 2 ;;
    --branch) BRANCH="$2"; shift 2 ;;
    --require-pr) REQUIRE_PRS+=("$2"); shift 2 ;;
    -h|--help) usage ;;
    *) echo "unknown arg: $1" >&2; usage ;;
  esac
done
[[ -z "$RELEASE" || -z "$NEXT" ]] && usage
: "${OSSRH_USERNAME:?must be set (Central Portal user token name)}"
: "${OSSRH_PASSWORD:?must be set (Central Portal user token password)}"

if [[ -f .sdkmanrc && -s "$HOME/.sdkman/bin/sdkman-init.sh" ]]; then
  set +u; source "$HOME/.sdkman/bin/sdkman-init.sh"; sdk env > /dev/null; set -u
fi

ok()   { printf '  \033[32m✓\033[0m %s\n' "$*"; }
warn() { printf '  \033[33m⚠\033[0m %s\n' "$*"; }
fail() { printf '  \033[31m✗\033[0m %s\n' "$*" >&2; exit 1; }
hdr()  { printf '\n→ %s\n' "$*"; }

# 1. Tree state
hdr "Tree state"
[[ -z "$(git status --porcelain)" ]] || fail "working tree not clean (run 'git status')"
ok "working tree clean"

CURRENT=$(git rev-parse --abbrev-ref HEAD)
[[ "$CURRENT" == "$BRANCH" ]] || fail "on '$CURRENT', expected '$BRANCH'"
ok "on branch $BRANCH"

git fetch upstream "$BRANCH" --tags --quiet
LOCAL_HEAD=$(git rev-parse HEAD)
UPSTREAM_HEAD=$(git rev-parse "upstream/$BRANCH")
if [[ "$LOCAL_HEAD" != "$UPSTREAM_HEAD" ]]; then
  AHEAD=$(git rev-list --count "upstream/$BRANCH..HEAD")
  BEHIND=$(git rev-list --count "HEAD..upstream/$BRANCH")
  fail "local $BRANCH diverged from upstream (ahead $AHEAD / behind $BEHIND) — reconcile first"
fi
ok "HEAD == upstream/$BRANCH (${LOCAL_HEAD:0:10})"

for SHA in "${REQUIRE_PRS[@]:-}"; do
  [[ -z "$SHA" ]] && continue
  git merge-base --is-ancestor "$SHA" HEAD || fail "required commit $SHA not reachable from HEAD"
  ok "required commit ${SHA:0:10} reachable"
done

# 2. Env & credentials
hdr "Env & credentials"
command -v mvn > /dev/null || fail "mvn not on PATH"
JAVA_VER=$(java -version 2>&1 | awk -F\" 'NR==1{print $2}')
[[ "$JAVA_VER" == 11.* ]] || warn "java $JAVA_VER — expected 11.x"
ok "java $JAVA_VER, mvn $(mvn -v 2>/dev/null | head -1 | awk '{print $3}')"

[[ -f "$HOME/.m2/settings.xml" ]] || fail "~/.m2/settings.xml missing (see Maven release guide §Setup)"
grep -q '<id>central</id>' "$HOME/.m2/settings.xml" \
  || warn "<server id=central> not in settings.xml — release:perform upload may fail"
ok "~/.m2/settings.xml present"

# Prefer the keyname Maven is configured to use (<gpg.keyname> in settings.xml);
# fall back to the first secret key in the keyring if none is configured.
GPG_KEYNAME=$(grep -oE '<gpg\.keyname>[^<]+' "$HOME/.m2/settings.xml" 2>/dev/null | head -1 | sed 's/<gpg\.keyname>//')
if [[ -n "$GPG_KEYNAME" ]]; then
  FINGERPRINT=$(gpg --list-keys --with-colons "$GPG_KEYNAME" 2>/dev/null | awk -F: '/^fpr:/{print $10; exit}')
  [[ -n "$FINGERPRINT" ]] || fail "gpg.keyname=$GPG_KEYNAME from settings.xml not found in keyring"
  ok "Maven gpg.keyname: $GPG_KEYNAME → fingerprint $FINGERPRINT"
else
  FINGERPRINT=$(gpg --list-secret-keys --with-colons --fingerprint 2>/dev/null | awk -F: '/^fpr:/{print $10; exit}')
  [[ -n "$FINGERPRINT" ]] || fail "no GPG secret key in default keyring"
  warn "no <gpg.keyname> in ~/.m2/settings.xml — falling back to first secret key"
  ok "signing fingerprint: $FINGERPRINT"
fi

HTTP=$(curl -so /dev/null -w "%{http_code}" "https://keys.openpgp.org/vks/v1/by-fingerprint/$FINGERPRINT")
[[ "$HTTP" == "200" ]] || fail "key $FINGERPRINT not on keys.openpgp.org (HTTP $HTTP) — upload via https://keys.openpgp.org/upload"
ok "key reachable on keys.openpgp.org"

# 3. POM hygiene
hdr "POM hygiene"
STALE=$(grep -RIln --include=pom.xml -E 'oss\.sonatype\.org|sonatype-nexus-staging' . 2>/dev/null || true)
[[ -z "$STALE" ]] || fail "stale OSSRH refs in:
$STALE"
ok "no stale OSSRH URLs"

if grep -q '<developers>' pom.xml 2>/dev/null; then
  ok "<developers> in top-level pom.xml"
else
  PARENT_VER=$(mvn -q help:evaluate -Dexpression=project.parent.version -DforceStdout 2>/dev/null || echo unknown)
  warn "<developers> not in top-level POM — relying on parent (version: $PARENT_VER)"
fi

# 4. Build smoke
hdr "Build smoke (mvn clean install)"
mvn -B clean install > /tmp/release-build.log 2>&1 \
  || { echo; tail -30 /tmp/release-build.log; fail "build failed (see /tmp/release-build.log)"; }
ok "clean install passed"

# 5. Styling pass — auto-fix NOTICE/license/javadoc drift, then verify
#    notice:check and license:check are what mvn release:prepare's `clean verify`
#    runs; running them here surfaces drift before the destructive release plugin.
hdr "Styling pass + NOTICE/license verification"

# Run formatters/generators (best-effort; warnings only)
mvn -B notice:generate license:format javadoc:fix > /tmp/release-styling.log 2>&1 \
  || warn "styling pass non-zero exit (see /tmp/release-styling.log)"

# Verify check goals pass. If notice:check fails, target/NOTICE.expected contains
# the correct content — copy it over and re-verify.
if ! mvn -B notice:check license:check > /tmp/release-checks.log 2>&1; then
  if [[ -f target/NOTICE.expected ]] && ! diff -q target/NOTICE.expected NOTICE > /dev/null 2>&1; then
    cp target/NOTICE.expected NOTICE
    warn "NOTICE refreshed from target/NOTICE.expected"
    if ! mvn -B notice:check license:check > /tmp/release-checks.log 2>&1; then
      echo; tail -30 /tmp/release-checks.log
      fail "notice:check / license:check still failing after NOTICE auto-fix"
    fi
  else
    echo; tail -30 /tmp/release-checks.log
    fail "notice:check / license:check failed (see /tmp/release-checks.log)"
  fi
fi

# Anything left in the working tree is drift the operator must review + commit
if ! git diff --quiet; then
  printf '\ndrift detected (auto-fixed; operator must review + commit):\n'
  git diff --stat
  fail "review the diff above, commit as 'chore: pre-release prep', push to upstream/$BRANCH, then re-run this script"
fi
ok "no drift; notice:check + license:check pass"

# 6. Artifact discovery
hdr "Artifact discovery"
GROUP_ID=$(mvn -q help:evaluate -Dexpression=project.groupId -DforceStdout 2>/dev/null)
ARTIFACT_ID=$(mvn -q help:evaluate -Dexpression=project.artifactId -DforceStdout 2>/dev/null)
GROUP_PATH=$(echo "$GROUP_ID" | tr . /)
ok "groupId:    $GROUP_ID"
ok "artifactId: $ARTIFACT_ID"

CANARY_URL="https://repo1.maven.org/maven2/$GROUP_PATH/$ARTIFACT_ID/$RELEASE/$ARTIFACT_ID-$RELEASE.pom"
EXISTING=$(curl -sI -o /dev/null -w "%{http_code}" "$CANARY_URL")
[[ "$EXISTING" == "404" ]] || fail "$ARTIFACT_ID-$RELEASE already on Central (HTTP $EXISTING) — pick a new version"
ok "$ARTIFACT_ID-$RELEASE not on Central (HTTP 404, as expected)"

# 7. Summary
cat <<EOF

═══ RELEASE SUMMARY ═══
  Repo:          $(basename "$PWD")
  Branch:        $BRANCH @ ${LOCAL_HEAD:0:10}
  HEAD subject:  $(git log -1 --pretty=%s)
  groupId:       $GROUP_ID
  artifactId:    $ARTIFACT_ID
  Release ver:   $RELEASE          (tag: $ARTIFACT_ID-$RELEASE)
  Next snapshot: $NEXT
  Signing key:   $FINGERPRINT
  ossrh user:    $OSSRH_USERNAME
  Canary URL:    $CANARY_URL
═══════════════════════

EOF
read -r -p "Proceed with release? (y/N) " ANS
[[ "$ANS" == "y" || "$ANS" == "Y" ]] || { echo "aborted"; exit 0; }

# 8. Cut release
hdr "mvn release:clean release:prepare release:perform"
mvn -B release:clean release:prepare release:perform \
  -DautoVersionSubmodules=true \
  -DreleaseVersion="$RELEASE" \
  -DdevelopmentVersion="$NEXT" \
  -Dtag="$ARTIFACT_ID-$RELEASE"
ok "release cut + tag pushed"

# 9. OSSRH POST
hdr "POST staged artifacts to Central Portal"
AUTH=$(printf '%s:%s' "$OSSRH_USERNAME" "$OSSRH_PASSWORD" | base64)
HTTP=$(curl -s -o /tmp/release-ossrh.out -w "%{http_code}" -X POST \
  "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$GROUP_ID" \
  -H "Authorization: Bearer $AUTH")
[[ "$HTTP" == "200" ]] || fail "OSSRH POST HTTP $HTTP (see /tmp/release-ossrh.out)"
ok "OSSRH POST: HTTP 200"

# 10. Next steps
cat <<EOF

✓ Release prepared.

Next steps:
  1. Log into https://central.sonatype.com
  2. Navigate to Deployments → verify $ARTIFACT_ID:$RELEASE → click Publish
  3. Wait for propagation (~10-30 min); poll:
       curl -sI -o /dev/null -w "HTTP %{http_code}\\n" "$CANARY_URL"
  4. After propagation: draft GitHub release notes, bump pin in uPortal-start

EOF
