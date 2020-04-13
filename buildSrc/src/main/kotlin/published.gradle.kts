import com.jfrog.bintray.gradle.BintrayExtension

plugins {
  id("nebula.maven-publish")
  id("nebula.source-jar")
  id("nebula.nebula-bintray-publishing")
}

plugins.withId("kotlin") {
  tasks.withType<Javadoc> {
    enabled = true
  }
}

publishing {
  publications {
    getByName<MavenPublication>("nebula") {
      pom {
        url.set("https://github.com/justwrote/ktor-swagger")
        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("justwrote")
            name.set("Dominik Schmidt")
            email.set("justwrote@gmail.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/justwrote/ktor-swagger.git")
          developerConnection
            .set("scm:git:ssh://github.com/justwrote/ktor-swagger.git")
          url.set("http://github.com/justwrote/ktor-swagger/")
        }
      }
    }
  }
}

bintray {
  user = System.getenv("BINTRAY_USER")
  key = System.getenv("BINTRAY_KEY")
  pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
    userOrg = "justwrote"
    repo = "maven"
    websiteUrl = "https://github.com/justwrote/ktor-swagger/"
    issueTrackerUrl = "https://github.com/justwrote/ktor-swagger/issues"
    vcsUrl = "https://github.com/justwrote/ktor-swagger.git"
    setLicenses("Apache-2.0")
    setLabels("openapi","swagger", "ktor", "kotlin")
  })
}
