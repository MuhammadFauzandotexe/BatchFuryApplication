#!/bin/bash

# ===============================
# CONFIGURATION
# ===============================
APP_NAME="BatchFury"
JAR_NAME="BatchFury-0.0.1-SNAPSHOT.jar"
PROFILE=${1:-local}
REPORT_DATE=${2:-$(date '+%Y-%m-%d')}
BUILD_DIR="build"
LOG_DIR="logs"

# File logs
BATCH_LOG="$LOG_DIR/batch-${PROFILE}-${REPORT_DATE}.log"
GC_LOG="$LOG_DIR/gc-${PROFILE}-${REPORT_DATE}.log"
HEAP_DUMP="$LOG_DIR/heapdump-${PROFILE}-${REPORT_DATE}.hprof"

# ===============================
# PREPARE
# ===============================
echo "🔧 Profile: $PROFILE"
echo "📝 Log File: $BATCH_LOG"
echo "♻️ GC Log: $GC_LOG"

mkdir -p "$BUILD_DIR"
mkdir -p "$LOG_DIR"

# ===============================
# BUILD STEP
# ===============================
echo "🔨 Building the application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "❌ Build failed. Exiting..."
  exit 1
fi

cp -v "target/$JAR_NAME" "$BUILD_DIR/"

# ===============================
# RUN STEP
# ===============================
echo "🚀 Running the batch job..."

START_TIME=$(date +%s)

java -Xms512m -Xmx1g \
  -XX:+UseG1GC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath="$HEAP_DUMP" \
  -Dfile.encoding=UTF-8 \
  -verbose:gc \
  -jar "$BUILD_DIR/$JAR_NAME" \
  --spring.profiles.active="$PROFILE" \
  --reportDate="$REPORT_DATE" \
  | tee "$BATCH_LOG"

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo "✅ Job done in $DURATION seconds"
