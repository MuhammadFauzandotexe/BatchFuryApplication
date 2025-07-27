#!/bin/bash

# ===============================
# CONFIGURATION
# ===============================
APP_NAME="BatchFury"
JAR_NAME="BatchFury-0.0.1-SNAPSHOT.jar"
REMOTE_USER="your_user"
REMOTE_HOST="your.server.com"
REMOTE_DIR="/home/your_user/batch-run"
ACTIVE_PROFILE="local"
REPORT_DATE=$(date '+%Y-%m-%d')

# Optional: Enable log capture
CAPTURE_LOG=true
LOCAL_LOG_FILE="logs/remote_batch_$(date '+%Y%m%d_%H%M%S').log"

# ===============================
# BUILD STEP
# ===============================
echo "🔨 Building the application locally..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "❌ Build failed. Exiting..."
  exit 1
fi

# ===============================
# SCP TO REMOTE
# ===============================
echo "📤 Copying JAR to remote server..."
scp target/$JAR_NAME $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/

if [ $? -ne 0 ]; then
  echo "❌ Failed to copy file to remote server. Exiting..."
  exit 1
fi

# ===============================
# RUN REMOTELY
# ===============================
echo "🚀 Running JAR on remote host [$REMOTE_HOST] with profile [$ACTIVE_PROFILE]"

REMOTE_COMMAND="cd $REMOTE_DIR && java -Xms512m -Xmx1g -jar $JAR_NAME --spring.profiles.active=$ACTIVE_PROFILE --reportDate=$REPORT_DATE"

if [ "$CAPTURE_LOG" = true ]; then
  echo "📥 Capturing log locally to $LOCAL_LOG_FILE"
  ssh $REMOTE_USER@$REMOTE_HOST "$REMOTE_COMMAND" | tee "$LOCAL_LOG_FILE"
else
  ssh $REMOTE_USER@$REMOTE_HOST "$REMOTE_COMMAND"
fi
