#!/usr/bin/env bash
# Mengganti username "felippp" dengan username Anda di seluruh proyek.
# Pemakaian: ./rename-username.sh ifs18005   (jalankan dari root proyek, lalu ganti nama folder proyek)
set -euo pipefail
NEW="${1:?Pemakaian: ./rename-username.sh <username>}"
OLD="felippp"
mv "src/main/java/org/delcom/${OLD}springbootcashflow" "src/main/java/org/delcom/${NEW}springbootcashflow"
mv "src/test/java/org/delcom/${OLD}springbootcashflow" "src/test/java/org/delcom/${NEW}springbootcashflow"
grep -rIl "$OLD" . --exclude-dir=.git --exclude-dir=target --exclude=rename-username.sh \
  | xargs sed -i "s/${OLD}/${NEW}/g"
echo "Selesai. Ganti nama folder proyek menjadi ${NEW}-p5-pbo2026-springboot-cashflow"
