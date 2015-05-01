JET REMOTE
====

Sistema de Control de Embarcaciones para Water Sports Centers.

Jet Remote es un proyecto de código abierto cuyo objetivo es el desarrollo y producción de soluciones que incrementen la seguridad de las motos acuáticas (también conocidas como Jet-Ski o Personal WaterCraft).

Jet Remote permite desconectar inmediatamente embarcaciones desde la orilla en caso de una conducta inadecuada por parte del piloto o en caso de existir riesgo de abordaje. 

Jet Remote Server implementa un servidor Bluetooth para el uso de Jet Remote con la app para Android.


<h3>Software necesario</h3>
- JetRemote 868 MHz
- Sistema Operativo (Rasbian, Pidora, ArchLinux, FreeBSD,...)
- Oracle Java 8 
- Librería Java RxTx
- Librería Bluecove


<h3>Hardware necesario</h3>

<h4>Para el Control a Distancia</h4>
- [Raspberry Pi B, B+ y Pi2] (http://www.raspberrypi.org/)
- [Tarjeta SDHC Speed Class 10 (de al menos 8Gb)] (http://es.wikipedia.org/wiki/SDHC)
- XBee Pro 868 (RPSMA)
- Antena 868MHz RP-SMA
- Pantalla TFT Raspberry Pi - 2.8" táctil (PiTFT)
- Batería LiPo 6000-1200mAh / 3.7V
- Cargador de batería LiPo USB y DC
- Conversor DC 5V (5A)
- Bluetooth USB

<h4>Para cada Embarcación (1 milla)</h4>
- XBee Pro 868 (RPSMA)
- Antena 868MHz RP-SMA



====
JetRemote ahora utiliza Maven para la gestión de dependencias y construcción del proyecto. 

Instalación en Raspbian:

sudo apt-get install maven

Para generar el proyecto:

mvn clean install assembly:single

====

Si descubres algún error, deseas aportar alguna sugerencia o solicitar una funcionalidad, por favor utiliza la [página de issues de Github](https://github.com/linuxgc/PiWC/issues) para avisarnos.


JetRemote es libre bajo la [GNU General Public License v.3] (http://www.gnu.org/licenses/gpl.html)
