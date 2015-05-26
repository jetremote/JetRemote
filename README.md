JET REMOTE KeyPad
====

Sistema de Control de Embarcaciones para Water Sports Centers.


Esta versión está diseñada para teclados matrices de membrana de 3x4 o 4x4



<h3>Software necesario</h3>
- JetRemote KeyPad
- Sistema Operativo (Rasbian, Pidora, ArchLinux, FreeBSD,...)
- Oracle Java 8 
- Librería Java RxTx


<h3>Hardware necesario</h3>

<h4>Para el Mando Jet Remote</h4>
- [Raspberry Pi B, B+ y Pi2] (http://www.raspberrypi.org/)
- [Tarjeta SDHC Speed Class 10 (de al menos 8Gb)] (http://es.wikipedia.org/wiki/SDHC)
- XBee Pro 868 RPSMA
- Antena 868MHz RPSMA
- Teclado membrana Jet Remote
- Batería LiPo 6000-1200mAh / 3.7V
- Cargador de batería LiPo USB y DC
- Conversor DC 5V (5A)


<h4>Para cada Embarcación</h4>
- XBee Pro 868 (RPSMA)
- Antena 868MHz RP-SMA
- PCB Jet Remote
- Caja de Aluminio para PCB Jet Remote



====
JetRemote ahora utiliza Maven para la gestión de dependencias y construcción del proyecto. 

Instalación en Raspbian:

sudo apt-get install maven

Para generar el proyecto:

mvn clean install assembly:single

====

Si descubres algún error, deseas aportar alguna sugerencia o solicitar una funcionalidad, por favor, utiliza la [página de issues de Github](https://github.com/linuxgc/PiWC/issues) para avisarnos.


JetRemote es libre bajo la [GNU General Public License v.3] (http://www.gnu.org/licenses/gpl.html)
