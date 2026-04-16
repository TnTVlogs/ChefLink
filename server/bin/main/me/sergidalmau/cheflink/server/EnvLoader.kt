package me.sergidalmau.cheflink.server

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvBuilder
import java.io.File

object EnvLoader {
    private fun jarDirOrNull(): File? {
        return runCatching {
            val uri = EnvLoader::class.java.protectionDomain.codeSource.location.toURI()
            val file = File(uri)
            if (file.isFile) file.parentFile else file
        }.getOrNull()
    }

    private fun loadFromDirectory(dir: File): Dotenv {
        val builder: DotenvBuilder = Dotenv.configure()
            .directory(dir.absolutePath)
            .filename(".env")
            .ignoreIfMissing()
        return builder.load()
    }

    fun load(): Pair<Dotenv, String> {
        val overrideDir = System.getenv("CHEFLINK_ENV_DIR")?.takeIf { it.isNotBlank() }?.let(::File)
        val jarDir = jarDirOrNull()
        val cwd = File(System.getProperty("user.dir"))

        val candidates = listOfNotNull(overrideDir, jarDir, cwd).distinctBy { it.absolutePath }
        for (dir in candidates) {
            val envFile = File(dir, ".env")
            if (envFile.exists()) {
                return loadFromDirectory(dir) to envFile.absolutePath
            }
        }

        // Not found: still return a Dotenv that won't throw.
        val fallback = loadFromDirectory(cwd)
        return fallback to "(missing: searched ${candidates.joinToString { it.absolutePath }})"
    }
}
