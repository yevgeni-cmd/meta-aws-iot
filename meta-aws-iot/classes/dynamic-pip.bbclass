python calculate_pip_checksum() {
    import os
    import hashlib
    import bb

    dl_dir = d.getVar("DL_DIR")
    package = d.getVar("PIPOE_PYPI_PACKAGE")
    version = d.getVar("PIPOE_VERSION")
    tar_file = os.path.join(dl_dir, f"{package}-{version}.tar.gz")

    if os.path.exists(tar_file):
        with open(tar_file, "rb") as f:
            checksum = hashlib.sha256(f.read()).hexdigest()
        d.setVar("SRC_URI[sha256sum]", checksum)
        bb.note(f"Calculated SHA256 for {package}-{version}.tar.gz: {checksum}")
    else:
        bb.warn(f"Source tarball {tar_file} not found. Ensure it is downloaded first.")

    if d.getVar("YOCTO_ENV") == "dev":
        d.setVar("BB_STRICT_CHECKSUM", "0")
        bb.note("Dev mode: Skipping checksum verification")

}

addtask calculate_pip_checksum before do_fetch after do_fetch