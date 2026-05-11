dependencies {
    api("org.apache.commons:commons-lang3:3.13.0")
    api("org.apache.commons:commons-math3:3.6.1")
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}