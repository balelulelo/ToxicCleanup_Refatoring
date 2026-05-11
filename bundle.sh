#!/bin/bash

if [ -f submission.zip ]; then
  mkdir -p past_submissions/
  old=$(date +%s)
  echo "Back up old submission as submission-${old}\n"
  mv submission.zip past_submissions/submission-${old}.zip
fi

zip -r submission.zip \
    src \
    test/toxiccleanup/builder/machines/MachinesManagerTest.java \
    test/toxiccleanup/builder/machines/PumpTest.java \
    ai \
    Justification.pdf \
    -x \*package-info\* \
    \*.DS_Store\* \
    \*.class
