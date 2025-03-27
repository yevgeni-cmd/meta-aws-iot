SUMMARY = "AWS IoT Device SDK v2 for Python with Boto3"
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python-v2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

require conf/distro/include/versions.inc
inherit pipoe dynamic-pip

PIPOE_PYPI_PACKAGE = "awsiotsdk"
PIPOE_VERSION = "${AWS_IOT_SDK_VERSION}"

# Include Boto3 as an additional dependency
PIPOE_EXTRA_PACKAGES = "boto3"

RDEPENDS:${PN} += "python3-core python3-boto3"