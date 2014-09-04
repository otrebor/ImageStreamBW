#!/bin/bash
echo "Sequential invocation"
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=1 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  util.ImageStreamBWSeq ) 2>&1
echo "NOT INVOKING GC >"
echo "=============================================================================================================="
echo "#CPU #########################################################################################################"
echo "=============================================================================================================="
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=1 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 1 -maxincomingfile 10 ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=2 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 2 -maxincomingfile 10 ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=4 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 4 -maxincomingfile 10 ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=8 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 8 -maxincomingfile 10 ) 2>&1
echo "=============================================================================================================="
echo "#GPU MULTIPLE KERNELS ########################################################################################"
echo "=============================================================================================================="
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=1 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 1 -maxincomingfile 10 -gpu auto ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=2 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 2 -maxincomingfile 10 -gpu auto ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=4 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 4 -maxincomingfile 10 -gpu auto ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=8 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 8 -maxincomingfile 10 -gpu auto ) 2>&1
echo "=============================================================================================================="
echo "#GPU SINGLE KERNEL ###########################################################################################"
echo "=============================================================================================================="
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=1 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 1 -maxincomingfile 10 -gpu auto -kernel single ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=2 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 2 -maxincomingfile 10 -gpu auto -kernel single ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=4 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 4 -maxincomingfile 10 -gpu auto -kernel single ) 2>&1
time ( java -Xms512m -Xmx1024m -XX:+UseParallelGC -XX:PermSize=256M -XX:MaxPermSize=512M -XX:ParallelGCThreads=8 -XX:+UseAdaptiveSizePolicy -classpath lib/jopt-simple-3.3.jar:lib/javacl-1.0.0-RC1-shaded.jar:lib/skandium-1.0b2.jar:bin/  isbw.ImageStreamBW ../images/ ../bwimages/ -v -pardeg 8 -maxincomingfile 10 -gpu auto -kernel single ) 2>&1