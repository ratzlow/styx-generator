# Intro
Generate a trading domain model from an accepted industry 
standard vocabularies - here the FIX protocol. Next to the 
POJO interface it offers an expressive API with generic support. Key idea is to benefit from the ongoing evolution of an 
accepted standard but yet allow generous customization.

# Environment
It is highly recommended to install the prerequisites using SDKMAN (https://sdkman.io)
- JDK: 8.x
- Groovy: 2.5.x
- Gradle: 6.0.x


# TODOs

## Create code generator template engine
- only generate classes of trees in DomainModel-FIXxx.xml

## Groovy
- externalize type mapping for fields