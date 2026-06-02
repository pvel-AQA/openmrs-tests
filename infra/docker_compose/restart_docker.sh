#!/bin/bash
cd "$(dirname "$0")"

echo ">>> Stop Docker Compose"
docker compose down

echo ">>> Docker pull all browser images"

# Путь до файла
json_file="./config/browsers.json"

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Извлекаем все значения .image через jq
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file" | tr -d '\r')

# Пробегаем по каждому образу и выполняем docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

#echo ">>> Start Docker Compose"
#docker compose up -d
echo ">>> Start core services (db, backend, frontend, nginx)"
docker compose up -d db backend frontend nginx