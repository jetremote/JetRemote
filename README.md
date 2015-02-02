JET REMOTE
====

Sistema de Control de Embarcaciones para Water Sports Centers.

Jet Remote es una aplicación multiplataforma que permite desconectar embarcaciones a largas distancias.

<h3>Software necesario</h3>
- JetRemote.jar
- Sistema Operativo (Rasbian, Pidora, ArchLinux, FreeBSD,...)
- Oracle Java 8 
- Librería Java RxTx


<h3>Hardware necesario</h3>

<h4>Para el Control a Distancia</h4>
- [Raspberry Pi] (http://www.raspberrypi.org/)
- [Tarjeta SDHC Speed Class 10 (de al menos 4Gb)] (http://es.wikipedia.org/wiki/SDHC)
- [XBee Pro 63mW serie 2B (RPSMA)]
- [Antena 2.4GHz RP-SMA]
- [Pantalla TFT Raspberry Pi - 2.8" táctil (PiTFT)]
- [Batería LiPo 6000mAh / 3.7V]
- [Cargador de batería LiPo USB y DC]
- [Conversor DC 5V (5A)]

<h4>Para cada Embarcación (2 millas)</h4>
- [Kit relé 20A]
- [XBee Pro 63mW serie 2B (RPSMA)]
- [Conversor DC 12V]()
- [Antena 2.4GHz RP-SMA]

<h4>Para cada Embarcación (9 millas, 28 millas con antena de alta ganancia)</h4>
- Kit relé 20A
- [XBee-PRO® 900HP](http://www.digi.com/products/wireless-wired-embedded-solutions/zigbee-rf-modules/point-multipoint-rfmodules/xbee-pro-900hp)
- Conversor DC 12V
- Antena 2.4GHz RP-SMA




====
<h1>Desarrolladores</h1>
JetRemote utiliza el framework [RemoteVM de Abstract Horizon](http://remotevm.abstracthorizon.org/index.html) para ejecutar y depurar la aplicación directamente sobre la raspberry Pi. Para saber más sobre RemoteVM y su uso visite los [Tutoriales](http://remotevm.abstracthorizon.org/tutorials.html)


Si descubres algún error, deseas aportar alguna sugerencia o solicitar una funcionalidad, por favor utiliza la [página de issues de Github](https://github.com/linuxgc/PiWC/issues) para avisarnos.


JetRemote es libre bajo la [GNU General Public License v.3] (http://www.gnu.org/licenses/gpl.html)
