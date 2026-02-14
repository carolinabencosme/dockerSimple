# dockerSimple

## Build y run con puerto configurable

El contenedor usa `PORT` para definir el puerto interno de la aplicación.

### Docker build/run (ejemplo con puerto 8081)

```bash
docker build --build-arg PORT=8081 -t wornux/urlshortener:local .
docker run --rm -e PORT=8081 -p 8081:8081 wornux/urlshortener:local
```

### Docker Compose (usando `APP_PORT`)

`docker-compose.yml` mantiene el mapeo en función de `APP_PORT`, y además envía ese valor a `PORT` y al build arg `PORT`:

```bash
APP_PORT=8081 docker compose up --build
```
