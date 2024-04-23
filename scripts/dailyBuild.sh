#!/bin/bash
while getopts t:d:b:u: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}" ;;
        d) DRIVER="${OPTARG}" ;;
        b) BUILD="${OPTARG}" ;;
        *) echo "Invalid option" ;;
    esac
done

echo "Testing daily OpenLiberty image"

sed -i "\#</containerRunOpts>#a<install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/""$DATE""/""$DRIVER""</runtimeUrl></install>" system/pom.xml inventory/pom.xml
cat system/pom.xml inventory/pom.xml

 sudo ../scripts/testApp.sh
