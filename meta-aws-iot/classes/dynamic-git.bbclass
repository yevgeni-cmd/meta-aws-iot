python calculate_git_srcrev() {
    import subprocess
    import bb

    src_uri = d.getVar("SRC_URI")
    if not src_uri:
        bb.fatal("SRC_URI is not set")

    repo_url = src_uri.split(";")[0].split("git://")[1]
    tag = d.getVar("PV")
    
    command = ["git", "ls-remote", f"https://{repo_url}", tag]
    result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    if result.returncode != 0:
        bb.error(f"Failed to get commit hash: {result.stderr.decode()}")
        return

    output = result.stdout.decode().strip()
    commit_hash = output.split("\t")[0]

    d.setVar("SRCPV", commit_hash)
    d.setVar("PV", f"{tag}+git{commit_hash}")
    bb.note(f"Resolved {tag} to commit {commit_hash}")

}

addtask calculate_git_srcrev before do_fetch after do_fetch