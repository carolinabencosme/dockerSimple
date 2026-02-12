# Configuración
LOG_DIR = logs
DEV_LOG = $(LOG_DIR)/dev.log
PROD_LOG = $(LOG_DIR)/prod.log
ERROR_LOG = $(LOG_DIR)/error.log

# Determinar entorno (default: dev)
ENV ?= dev

# Comprobar si el log dir existe
$(shell mkdir -p $(LOG_DIR))

# Build para producción (genera el JAR con shadowJar)
build:
	@echo "Compilando aplicación con shadowJar..."
	./gradlew shadowJar

# Ejecutar en modo desarrollo
run-dev:
	@echo "Ejecutando en modo desarrollo..."
	./gradlew run > $(DEV_LOG) 2>&1

# Ejecutar en modo producción con el JAR generado
run-prod: build
	@echo "Ejecutando en modo producción..."
	nohup java -jar build/libs/*.jar > $(PROD_LOG) 2> $(ERROR_LOG) &

# Ejecutar según el entorno
run:
ifeq ($(ENV),prod)
	$(MAKE) run-prod
else
	$(MAKE) run-dev
endif

# Limpieza de archivos generados
clean:
	@echo "Limpiando archivos generados..."
	./gradlew clean
	rm -rf $(LOG_DIR)

.PHONY: build run-dev run-prod run clean
