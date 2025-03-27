SUMMARY = "A demo image for aws-iot-greengrass-lite with A/B updates"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws-demos"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit core-image

DISTRO = "poky-altcfg"
QB_MEM = "-m 2048"
BOOT_SPACE = "512000"
IMAGE_OVERHEAD_FACTOR = "1"
DISTRO_FEATURES:append = " rauc"
DISTRO_FEATURES:remove = " bluetooth"
ENABLE_UART = "1"
RPI_USE_U_BOOT = "1"
WKS_FILE = "${THISDIR}/../../wic/sdimage-aws-iot-greengrass-lite_partition.wks.in"
IMAGE_FSTYPES = "ext4 wic.bz2 wic"
IMAGE_INSTALL:append = " rauc"
COPY_LIC_MANIFEST = "1"
COPY_LIC_DIRS = "1"
BB_GENERATE_MIRROR_TARBALLS = "1"
BB_GIT_SHALLOW = "1"
BB_GENERATE_SHALLOW_TARBALLS = "1"
INHERIT += "create-spdx image-buildinfo"
WIC_CREATE_EXTRA_ARGS = "--no-fstab-update"

IMAGE_INSTALL += "\
    ${CORE_IMAGE_EXTRA_INSTALL} \
    packagegroup-base \
    packagegroup-core-boot \
    arp-scan \
    python3-boto3 \
"

### AWS Components ###
IMAGE_INSTALL:append = " greengrass-lite aws-iot-device-sdk-python-v2 jq"
IMAGE_INSTALL:append = " python3-misc python3-venv python3-tomllib python3-ensurepip libcgroup python3-pip"

### RAUC for A/B Updates ###
CORE_IMAGE_EXTRA_INSTALL:append = " rauc-grow-data-part"
IMAGE_INSTALL:append:rpi = " greengrass-config-init"
IMAGE_INSTALL:append = " kernel-image kernel-modules"

### Tmux and Localization ###
IMAGE_INSTALL:append = " tmux glibc-utils localedef"
GLIBC_GENERATE_LOCALES = "en_US.UTF-8 UTF-8"

### SSH ###
IMAGE_INSTALL:append = " ssh openssh-sshd openssh-sftp openssh-scp"

### Miscellaneous ###
IMAGE_INSTALL:append = " sudo"

### Security Features ###
EXTRA_IMAGE_FEATURES ?= "allow-empty-password allow-root-login empty-root-password"

### Filesystem Configuration ###
IMAGE_FEATURES += "read-only-rootfs"
ROOTFS_POSTPROCESS_COMMAND += "rootfs_user_fstab;"

rootfs_user_fstab () {
    cat << EOF > ${IMAGE_ROOTFS}/${sysconfdir}/fstab
/dev/root            /                       auto    defaults                1 1
proc                /proc                   proc    defaults                0 0
devpts              /dev/pts                devpts  mode=0620,ptmxmode=0666,gid=5    0 0
tmpfs               /run                    tmpfs   mode=0755,nodev,nosuid,strictatime 0 0
tmpfs               /var/volatile           tmpfs   defaults                0 0
LABEL=boot          /boot                   vfat    defaults                0 0
LABEL=data          /data                   ext4    x-systemd.growfs        0 0
/data/etc/wpa_supplicant         /etc/wpa_supplicant     none    bind            0 0
/data/etc/greengrass             /etc/greengrass         none    bind            0 0
/data/etc/systemd/network         /etc/systemd/network     none    bind            0 0
/data/etc/systemd/system          /etc/systemd/system      none    bind            0 0
/data/var/lib/greengrass          /var/lib/greengrass      none    bind            0 0
/data/home             /home                   none    bind            0 0
EOF

    install -d -m 0755 ${IMAGE_ROOTFS}/data
    install -d ${IMAGE_ROOTFS}/data/etc/greengrass
    mv -f ${IMAGE_ROOTFS}/etc/greengrass/* ${IMAGE_ROOTFS}/data/etc/greengrass/ 2>/dev/null || true
    install -d ${IMAGE_ROOTFS}/data/etc/wpa_supplicant
    install -d ${IMAGE_ROOTFS}/data/etc/systemd/network
    mv -f ${IMAGE_ROOTFS}/etc/systemd/network/* ${IMAGE_ROOTFS}/data/etc/systemd/network 2>/dev/null || true
    install -d ${IMAGE_ROOTFS}/data/etc/systemd/system
    mv -f ${IMAGE_ROOTFS}/etc/systemd/system/* ${IMAGE_ROOTFS}/data/etc/systemd/system 2>/dev/null || true
    install -d ${IMAGE_ROOTFS}/data/var/lib/greengrass
    install -d ${IMAGE_ROOTFS}/data/home

    install -d ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/
    ln -sf /${libdir}/systemd/system/wpa_supplicant@.service \
        ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/wpa_supplicant@wlan0.service
    ln -sf /${libdir}/systemd/system/systemd-time-wait-sync.service \
        ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/systemd-time-wait-sync.service
}

# Integrate greengrass-nucleus-lite recipe

SUMMARY:append = " with AWS Greengrass Nucleus Lite"
DESCRIPTION:append = " including AWS Greengrass Nucleus Lite for resource-constrained devices."

LICENSE:append = " Apache-2.0"
LIC_FILES_CHKSUM:append = " file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

IMAGE_INSTALL:append = " greengrass-nucleus-lite"

# Adjust greengrass-nucleus-lite dependencies if needed.
RDEPENDS:greengrass-nucleus-lite:append = " bash systemd aws-iot-device-sdk-python-v2"

# Ensure greengrass-nucleus-lite service is enabled.
SYSTEMD_AUTO_ENABLE:greengrass-nucleus-lite = "enable"

# Adjust config directory and data directory
GREENGRASS_CONFIG_DIR = "${sysconfdir}/greengrass"
GREENGRASS_DATA_DIR = "/data/var/lib/greengrass"

# ensure the user is created
pkg_postinst_ontarget:greengrass-nucleus-lite() {
    #!/bin/sh
    if ! getent group ggc_group >/dev/null 2>&1; then
        groupadd -r ggc_group || echo "Failed to create ggc_group"
    fi
    if ! getent passwd ggc_user >/dev/null 2>&1; then
        useradd -r -M -N -g ggc_group -s /bin/false ggc_user || echo "Failed to create ggc_user"
    fi
}