# dynamic-checksum.bbclass
# Dynamically calculates and sets SHA256 checksum for the primary SRC_URI tarball

do_pre_fetch() {
    DL_DIR="${DL_DIR}"
    SRC_URI="${SRC_URI}"
    FILENAME=$(echo "$SRC_URI" | awk '{print $1}' | sed 's|.*/||')
    TAR_FILE="$DL_DIR/$FILENAME"

    if [ -f "$TAR_FILE" ]; then
        ACTUAL_CHECKSUM=$(sha256sum "$TAR_FILE" | awk '{print $1}')
        bbnote "Calculated SHA256 for $FILENAME: $ACTUAL_CHECKSUM"
        # Set the checksum dynamically for this build
        echo "SRC_URI[sha256sum] = \"$ACTUAL_CHECKSUM\"" > "${WORKDIR}/dynamic-checksum.inc"
        # Use a shell-safe way to update the variable
        export BB_ENV_PASSTHROUGH_ADDITIONS="$BB_ENV_PASSTHROUGH_ADDITIONS SRC_URI"
        export SRC_URI_sha256sum="$ACTUAL_CHECKSUM"
    else
        bbwarn "Source file $TAR_FILE not found! BitBake will fetch it first using the default checksum."
    fi
}

addtask do_pre_fetch before do_fetch