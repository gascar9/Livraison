#!/bin/bash
cd "$(dirname "$0")/out/production/Livraison"
java --enable-native-access=ALL-UNNAMED \
  -cp ".:$HOME/.m2/repository/org/jline/jline/3.25.1/jline-3.25.1.jar" \
  Main
