# Part 4: Systems follow-up (discussion)

Why `cd` is a shell built-in, the shell's cd flow, inode path resolution, and `cd -L` vs `cd -P`.

---

## 1. Why `cd` must be a shell built-in

- `cd` changes the current working directory of the calling process.
- If `cd` were an external program, the shell would `fork()` a child process to run it.
- The child could call `chdir()`, but only its own working directory would change.
- A parent shell directory cannot be changed child process
- Therefore, `cd` must be implemented inside the shell so the shell process itself calls `chdir()`.

---

## 2. High-level shell flow for handling a `cd`

- User enters `cd /path`.
- Shell parses the command and recognizes `cd` as a built-in.
- Shell expands the path (e.g., `~`, environment variables).
- Shell calls the `chdir()` system call.
- The kernel resolves the path and changes the shell process's current working directory.
- Shell updates `PWD` and `OLDPWD`.

---

## 3. How Linux resolves a path via inodes

For `cd /home/user/docs`:

- Start at the root directory (`/`).
- Find `home` in the root directory → get its inode.
- Read the `home` directory and find `user` → get its inode.
- Read the `user` directory and find `docs` → get its inode.
- Verify it's a directory and check permissions.
- Set that inode as the process's current working directory.

**Key point:** Infode is a manifesto file for a directory, and the inode contains the file/directory metadata and pointers to its data.

---

## 4. `cd -L` (Logical, default) vs `cd -P` (Physical)

### `cd -L` (Logical) — Default

- Keeps the path you typed.
- Doesn't resolve symbolic links in `PWD`.

### `cd -P` (Physical)

- Resolves symbolic links to the real directory.
- `PWD` stores the actual filesystem path.
