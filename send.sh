#!/bin/bash
# filepath: deploy-plugin.sh

# Nazwa pliku JAR (zmień jeśli masz inną nazwę)
JAR_NAME="xteleport-1.0.jar"

# Ścieżka do zbudowanego pliku
LOCAL_JAR="target/$JAR_NAME"

# Zdalny serwer i folder
REMOTE_USER="mati"
REMOTE_HOST="192.168.0.192"
REMOTE_DIR="/home/mati/minecraft/plugins"

# Sprawdź czy plik istnieje
if [ ! -f "$LOCAL_JAR" ]; then
    echo "Nie znaleziono pliku $LOCAL_JAR. Najpierw zbuduj plugin (mvn package)."
    exit 1
fi

# Kopiuj przez SCP (nadpisuje jeśli istnieje)
scp "$LOCAL_JAR" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/"

if [ $? -eq 0 ]; then
    echo "Plugin wysłany do $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/$JAR_NAME"
else
    echo "Błąd podczas wysyłania pluginu!"
fi
