SUMMARY = "ARP scanning and fingerprinting tool"
DESCRIPTION = "arp-scan sends ARP packets to hosts on the local network and displays any responses."
HOMEPAGE = "https://github.com/royhills/arp-scan"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

require conf/distro/include/versions.inc

inherit autotools dynamic-checksum

SRC_URI = "https://github.com/royhills/arp-scan/releases/download/${PV}/arp-scan-${PV}.tar.gz"
SRC_URI[sha256sum] = "a078fe8711ecbb8b99121c3d8be26ae7e7f339f11010ef61318be4f33394d012"

PV = "${ARP_SCAN_VERSION}"

S = "${WORKDIR}/arp-scan-${PV}"

DEPENDS += "libpcap"
RDEPENDS:${PN} += "bash"