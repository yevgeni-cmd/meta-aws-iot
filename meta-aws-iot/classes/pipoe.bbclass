# meta-aws-iot/classes/pipoe.bbclass
inherit python3native

PIPOE_PYPI_PACKAGE ??= "${BPN}"
PIPOE_VERSION ??= "${PV}"

do_install() {
    ${STAGING_BINDIR_NATIVE}/pip3 install --user -U "${PIPOE_PYPI_PACKAGE}==${PIPOE_VERSION}"
    if [ -n "${PIPOE_EXTRA_PACKAGES}" ]; then
        for pkg in ${PIPOE_EXTRA_PACKAGES}; do
            ${STAGING_BINDIR_NATIVE}/pip3 install --user -U "$pkg"
        done
    fi
}

EXPORT_FUNCTIONS do_install