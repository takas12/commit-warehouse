#!/bin/bash
set -e

declare -A aliases
aliases=(
	[3]='latest'
)

cd "$(dirname "$(readlink -f "$BASH_SOURCE")")"

versions=( */ )
versions=( "${versions[@]%/}" )
url='git://github.com/docker-library/docker-python'

echo '# maintainer: InfoSiftr <github@infosiftr.com> (@infosiftr)'

for version in "${versions[@]}"; do
	commit="$(git log -1 --format='format:%H' "$version")"
	fullVersion="$(grep -m1 'ENV PYTHON_VERSION ' "$version/Dockerfile" | cut -d' ' -f3)"
	majorVersion="$(echo "$fullVersion" | cut -d. -f1-2)"
	versionAliases=( $fullVersion $majorVersion $version ${aliases[$version]} )
	
	echo
	for va in "${versionAliases[@]}"; do
		echo "$va: ${url}@${commit} $version"
	done
done
