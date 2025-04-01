SUMMARY = "A demo image for aws-iot-greengrass-lite with A/B updates"
HOMEPAGE = "https://github.com/aws4embeddedlinux/meta-aws-demos"

LICENSE = "MIT"

inherit core-image

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

# overwrite the default fstab, adding customization for this image
cat << EOF > ${IMAGE_ROOTFS}/${sysconfdir}/fstab
/dev/root            /                    auto       defaults              1  1
proc                 /proc                proc       defaults              0  0
devpts               /dev/pts             devpts     mode=0620,ptmxmode=0666,gid=5      0  0
tmpfs                /run                 tmpfs      mode=0755,nodev,nosuid,strictatime 0  0
tmpfs                /var/volatile        tmpfs      defaults              0  0
LABEL=boot  /boot   vfat    defaults         0       0
LABEL=data     /data     ext4    x-systemd.growfs        0       0
/data/etc/wpa_supplicant             /etc/wpa_supplicant             none    bind            0       0
/data/etc/greengrass                 /etc/greengrass                 none    bind            0       0
/data/etc/systemd/network            /etc/systemd/network            none    bind            0       0
/data/etc/systemd/system            /etc/systemd/system            none    bind            0       0
/data/var/lib/greengrass      /var/lib/greengrass      none    bind            0       0
/data/home      /home      none    bind            0       0
EOF

install -d -m 0755 ${IMAGE_ROOTFS}/data

# copy those directories that should be present at the data partition to /data and just
# leave them empty as a mount point for the bind mount

install -d ${IMAGE_ROOTFS}/data/etc/greengrass
mv -f ${IMAGE_ROOTFS}/etc/greengrass/* ${IMAGE_ROOTFS}/data/etc/greengrass/

install -d ${IMAGE_ROOTFS}/data/etc/wpa_supplicant

install -d ${IMAGE_ROOTFS}/data/etc/systemd/network
mv -f ${IMAGE_ROOTFS}/etc/systemd/network/* ${IMAGE_ROOTFS}/data/etc/systemd/network

install -d ${IMAGE_ROOTFS}/data/etc/systemd/system
mv -f ${IMAGE_ROOTFS}/etc/systemd/system/* ${IMAGE_ROOTFS}/data/etc/systemd/system

install -d ${IMAGE_ROOTFS}/data/var/lib/greengrass

# decided to do here instead of a bbappend of wpa:supplicant
install -d ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/
ln -sf /${libdir}/systemd/system/wpa_supplicant@.service ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/wpa_supplicant@wlan0.service

# enable systemd-time-wait-sync as this is important for greengrass to have a correct clock
ln -sf /${libdir}/systemd/system/systemd-time-wait-sync.service ${IMAGE_ROOTFS}/${sysconfdir}/systemd/system/multi-user.target.wants/

install -d ${IMAGE_ROOTFS}/data/home
}