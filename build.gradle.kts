import org.polyfrost.gradle.util.noServerRunConfigs

plugins {
	id(libs.plugins.pgt.main.get().pluginId)
	id(libs.plugins.pgt.defaults.repo.get().pluginId)
	id(libs.plugins.pgt.defaults.loom.get().pluginId)
	id(libs.plugins.pgt.defaults.java.get().pluginId)
	alias(libs.plugins.blossom)
	id(libs.plugins.shadow.get().pluginId)
	alias(libs.plugins.kotlin) apply false
}

version = project.properties["mod_version"] as String
group = project.properties["maven_group"] as String

repositories {
	mavenCentral()
	maven("https://repo.polyfrost.org/releases")
	maven("https://maven.isxander.dev/releases")
	maven("https://maven.terraformersmc.com/releases")
}

java {
	toolchain {
		when(platform.mcVersion) {
			in 12001..12004 -> {
				languageVersion.set(JavaLanguageVersion.of(17))
			}
			else -> {
				languageVersion.set(JavaLanguageVersion.of(21))
			}
		}
	}
}

loom {
	noServerRunConfigs()
}

dependencies {
	// Mixin Extras because PGT doesn't include it
	implementation(annotationProcessor(libs.mixinextras.get())!!)

	val modMenuVersion = when(platform.mcVersion) {
		12004 -> "9.2.0-beta.2"
		else -> "10.0.0-beta.1"
	}

	val fabricApiVersion = when(platform.mcVersion) {
		12004 -> "0.97.0"
		else -> "0.98.0"
	}

	val yaclVersion = when(platform.mcVersion) {
		12004 -> "3.4.2+1.20.4-fabric"
		else -> "3.4.2+1.20.5-fabric"
	}

	modImplementation("com.terraformersmc:modmenu:${modMenuVersion}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}+${platform.mcVersionStr}")
	modImplementation("dev.isxander:yet-another-config-lib:${yaclVersion}")

	// DevAuth. Lets us log in with our own Minecraft account. Useful for testing tab features.
	modRuntimeOnly("me.djtheredstoner:DevAuth-${platform.loaderStr}:1.2.0")
}

tasks.processResources {
	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.version))
	}
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}
