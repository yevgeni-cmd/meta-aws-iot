DESCRIPTION = "An update bundle for aws-iot-greengrass-lite-image"

inherit bundle

RAUC_BUNDLE_VERSION = "v1.0.0"
RAUC_BUNDLE_DESCRIPTION = "Greengrass Lite Demo Bundle with A/B Updates"

RAUC_BUNDLE_SLOTS = "rootfs"
RAUC_SLOT_rootfs = "aws-iot-greengrass-lite-image"

RAUC_BUNDLE_COMPATIBLE = "${MACHINE}"
RAUC_BUNDLE_FORMAT = "verity"

RAUC_KEY_FILE = "${THISDIR}/files/development-1.key.pem"
RAUC_CERT_FILE = "${THISDIR}/files/development-1.cert.pem"