# ProjetoFinalPA

# XML Tag API Documentation

## Preparatory steps

Before utilizing the library one should make sure that inside the project settings the following maven libraries are present:

org.jetbrains.kotlin:kotlin-reflect:1.9.23
org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22
org.junit.jupiter:junit-jupiter:5.9.2

## Overview

This API provides a structured and efficient way to manipulate XML documents programmatically.

It is designed to manage XML tags, attributes, and hierarchical structures. 
The API consists of several key classes, including `Tag`, `Attributes`, `Document`, and `Translator`. 
These classes allow for the creation, modification, and pretty-printing of XML documents.
Tags should not be empty or contain spaces and there are checks to ensure the sanctity of the tags' names.

## Tutorials

### Using the code 

#### Tag Creation
To create a tag, use the Tag constructor:
```kotlin
val rootTag = Tag("root")
val childTag = Tag("child", rootTag)
```

#### Attribute Manipulation
To set, get, and remove attributes:
```kotlin
rootTag.attributes.setAttribute("version", "1.0")
val version = rootTag.attributes.getValues("version")
rootTag.attributes.removeAttribute("version")
```

```kotlin
    @Test
    fun attributeTesting() {
        // Creating a tag and setting attributes
        val planoTag = Tag("plano")
        planoTag.attributes.setAttribute("ano", "2023")
        planoTag.attributes.setAttribute("melhorquenoanopassado", "claroquenao")

        // Asserting that the attributes are set correctly
        assertEquals(setOf("ano", "melhorquenoanopassado"), planoTag.attributes.getAttributes())

        // Removing an attribute and checking the remaining attributes
        planoTag.attributes.removeAttribute("melhorquenoanopassado")
        assertEquals(setOf("ano"), planoTag.attributes.getAttributes())

        // Modifying an attribute value and checking the new value
        planoTag.attributes.setAttribute("ano", "2024")
        assertEquals("2024", planoTag.attributes.getValues("ano"))
    }

```

#### Document Creation
Create an XML document by specifying the root tag, version, and encoding:
```kotlin
val document = Document(rootTag, 1.0, "UTF-8")
```

```kotlin
    @Test
    fun createDocWithTag() {
        // Creating a document with a root tag and two child tags
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Asserting that the document's root tag has two children with the tag "curso"
        assertEquals(listOf(cursoTag, cursoTag2), myDoc.getRootElement().getChildren())
    }
```

#### Pretty Print
Generate a formatted XML string:
```kotlin
val xmlString = document.prettyPrint()
println(xmlString)
```

```kotlin
    @Test
    fun testPrettyPrint() {
        // Expected XML string
        val xmlString: String = """<?xml version="1.0" encoding="UTF-8"?>""" + "\n" +
                """<plano>""" + "\n\t" +
                """<curso>Mestrado em Engenharia Informática</curso>""" + "\n\t" +
                """<fuc codigo="M4310">""" + "\n\t\t" +
                """<nome>Programação Avançada</nome>""" + "\n\t\t" +
                """<ects>6.0</ects>""" + "\n\t\t" +
                """<avaliacao>""" + "\n\t\t\t" +
                """<componente nome="Quizzes" peso="20%"/>""" + "\n\t\t\t" +
                """<componente nome="Projeto" peso="80%"/>""" + "\n\t\t" +
                """</avaliacao>""" + "\n\t" +
                """</fuc>""" + "\n" +
                """</plano>"""
        
        // Creating the XML structure programmatically
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag, "")
        cursoTag.setText("Mestrado em Engenharia Informática")
        val fucTag = Tag("fuc", planoTag)
        fucTag.attributes.setAttribute("codigo", "M4310")
        Tag("nome", fucTag, "Programação Avançada")
        Tag("ects", fucTag, "6.0")
        val avaliacaoTag = Tag("avaliacao", fucTag)
        val componenteTag1 = Tag("componente", avaliacaoTag)
        componenteTag1.attributes.setAttribute("nome", "Quizzes")
        componenteTag1.attributes.setAttribute("peso", "20%")
        val componenteTag2 = Tag("componente", avaliacaoTag)
        componenteTag2.attributes.setAttribute("nome", "Projeto")
        componenteTag2.attributes.setAttribute("peso", "80%")
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Asserting that the pretty-printed XML matches the expected string
        assertEquals(xmlString, myDoc.prettyPrint())
    }
```

#### Global Operations
Set, edit, and remove attributes and tags globally: 

```kotlin
document.globalAttributeSetting("tag", "attribute", "value")
document.globalTagEditing("oldTag", "newTag")
document.globalTagRemoval("tag")
document.globalAttributeRemoval("tag", "attribute")
```

```kotlin
    
    @Test
    fun testGlobalAttributeInsertion() {
        // Creating a document with multiple tags
        val planoTag = Tag("plano")
        Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Setting a global attribute for all tags named "curso"
        myDoc.globalAttributeSetting("curso", "tipo", "diurno")
        val attributeMap = mutableMapOf<String, String>()
        attributeMap["tipo"] = "diurno"

        // Asserting that the attribute is set for all "curso" tags
        assertEquals(attributeMap.keys, myDoc.getRootElement().getChildren()[0].attributes.getAttributes())
        assertEquals(attributeMap.keys, myDoc.getRootElement().getChildren()[1].attributes.getAttributes())
    }

    @Test
    fun testGlobalTagEditing() {
        // Creating a document with multiple tags
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Editing the name of all tags named "curso" to "cadeira"
        myDoc.globalTagEditing("curso", "cadeira")

        // Asserting that the tag names have been changed
        assertEquals(listOf(cursoTag, cursoTag2), myDoc.getRootElement().getChildren())
    }

    @Test
    fun testGlobalTagRemoval() {
        // Creating a document with multiple tags
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        val cadeiraTag1 = Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Removing all tags named "curso"
        myDoc.globalTagRemoval("curso")

        // Asserting that the tags have been removed and their children reassigned correctly
        assertEquals(null, cursoTag.getParent())
        assertEquals(listOf(cadeiraTag1), cursoTag2.getChildren())
    }

    @Test
    fun testGlobalAttributeRemoval() {
        // Creating a document with multiple tags and attributes
        val planoTag = Tag("plano")
        Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        // Setting a global attribute and then removing it
        myDoc.globalAttributeSetting("curso", "tipo", "diurno")
        myDoc.globalAttributeRemoval("curso", "tipo")

        // Asserting that the attribute has been removed
        assertEquals(emptySet<String>(), myDoc.getRootElement().getChildren()[0].attributes.getAttributes())
    }
```

#### XPath Queries
Perform simple XPath queries:
```kotlin
val result = document.microXpath("root/child/tag")
```

```kotlin
    @Test
    fun testMicroXpath() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag, "")
        cursoTag.setText("Mestrado em Engenharia Informática")
        val fucTag = Tag("fuc", planoTag)
        fucTag.attributes.setAttribute("codigo", "M4310")
        Tag("nome", fucTag, "Programação Avançada")
        Tag("ects", fucTag, "6.0")
        val avaliacaoTag = Tag("avaliacao", fucTag)
        val componenteTag1 = Tag("componente", avaliacaoTag)
        componenteTag1.attributes.setAttribute("no     me", "Quizzes")
        componenteTag1.attributes.setAttribute("peso", "20%")
        val componenteTag2 = Tag("componente", avaliacaoTag)
        componenteTag2.attributes.setAttribute("nome", "Projeto")
        componenteTag2.attributes.setAttribute("peso", "80%")
        val myDoc = Document(planoTag, 1.0, "UTF-8")

        val stringList: MutableList<String> = mutableListOf()
        myDoc.microXpath("plano/fuc/avaliacao/componente").forEach {
            stringList.add(myDoc.prettyPrintLine(it))
        }
        assertEquals(
            listOf("""<componente nome="Quizzes" peso="20%"/>""", """<componente nome="Projeto" peso="80%"/>"""),
            stringList
        )
    }
```

Annotations
### Using Annotations

Annotations are used to define metadata and behavioral configurations for the XML elements within your document. Here, we explain how to use these annotations and what their parameters signify, as well as how they integrate with the internal DSL.

#### Defining Annotations

In our library, we use annotations to specify how Kotlin classes should be mapped to XML tags.

##### `@XmlTag`

The `@XmlTag` annotation is used to specify the XML tag name for a class. The variable tagName is what is used to set the final tags name. Property or class names take no effect

###### EXAMPLE

        (...)
        @XMLTag("avaliacao")
        val variable: Unit
        (...)

        
        result:
        (...)
        <avaliacao/>
        (...)

```kotlin
    @Test
    fun createSimpleXMLStructure() {
        val root = Tag("FUC")
        Tag("nome", root)
        Tag("ects", root)
        val doc = Document(root, 1.0, "UTF-8")

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("nome")
            val nome: String,
            @XMLTag("ects")
            val ects: Double,
        )

        assertEquals(doc.getRootElement.getTag, Translator(FUC("filler", 0.0)).createDoc().getRootElement.getTag)
        val sortedTags = doc.getRootElement.getChildren.sortedBy { it.getTag }
        assertEquals(
            sortedTags[0].getTag, Translator(
                FUC(
                    "nome", 1.0
                )
            ).createDoc().getRootElement.getChildren.sortedBy { it.getTag }[0].getTag
        )
    }
```

##### '@RootTag'

Signals that the XmlTag annotation associated is meant to be the root of the document. This tag must exist at least once in whatever generic class that is created.

###### EXAMPLE

        @XMLTag("avaliacao")
        @RootTag
        class Taggable(...){
        ...
        }

        result:
        <Taggable>
        (...)
        </Taggable>

```kotlin
    @Test
    fun createSimpleXMLStructure() {
        val root = Tag("FUC")
        Tag("nome", root)
        Tag("ects", root)
        val doc = Document(root, 1.0, "UTF-8")

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("nome")
            val nome: String,
            @XMLTag("ects")
            val ects: Double,
        )

        assertEquals(doc.getRootElement.getTag, Translator(FUC("filler", 0.0)).createDoc().getRootElement.getTag)
        val sortedTags = doc.getRootElement.getChildren.sortedBy { it.getTag }
        assertEquals(
            sortedTags[0].getTag, Translator(
                FUC(
                    "nome", 1.0
                )
            ).createDoc().getRootElement.getChildren.sortedBy { it.getTag }[0].getTag
        )
    }
```

##### '@AttributesAnnotation' 

This tag allows for the user to choose the name of the attribute and signals that the value held in the property will be the value associated to that tag's respective attribute name.
The name of the attribute is set by the annotation's variable attributeName

###### EXAMPLE

        (...)
        @XMLTag("avaliacao")
        @Attirbute("avaliacao")
        val name: String
        (...)

        result:
        (...)
        <avaliacao name = Daniel ></avaliacao>
        (...)

```kotlin
    @Test
    fun testSimpleStructureWithAttributes() {
        val root = Tag("FUC")
        val child1 = Tag("nome", root)
        val child2 = Tag("ects", root)
        root.attributes.setAttribute("codigo", "1")
        child1.attributes.setAttribute("nome", "nome")
        child2.attributes.setAttribute("ects", "1.0")
        val doc = Document(root, 1.0, "UTF-8")

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("FUC")
            @AttributesAnnotation("FUC")
            val codigo: String,
            @XMLTag("nome")
            @AttributesAnnotation("nome")
            val nome: String,
            @XMLTag("ects")
            @AttributesAnnotation("ects")
            val ects: Double
        )

        val genericToSpecific = Translator(FUC("1", "nome", 1.0)).createDoc()
        assertEquals(doc.prettyPrint(), genericToSpecific.prettyPrint())
    }
````

##### '@Text'

Shows that the value stored in the property below it contains the values to be used as the text of a given line

###### EXAMPLE

        (...)
        @XMLTag("avaliacao")
        @Text("avaliacao")
        val variable: String
        (...)

        result:
        (...)
        <avaliacao> I hope it all goes well </avaliacao>
        (...)

```kotlin
    @Test
    fun testFullXmlCreationWithEverything() {
        val root = Tag("FUC")
        root.attributes.setAttribute("codigo", "M4310")
        val child1 = Tag("nome", root)
        child1.setText("Programação Avançada")
        val child2 = Tag("ects", root)
        child2.setText("6.0")
        val child3 = Tag("avaliacao", root)
        val child4 = Tag("componente", child3)
        child4.attributes.setAttribute("nome", "Quizzes")
        child4.attributes.setAttribute("peso", "20")
        val child5 = Tag("componente", child3)
        child5.attributes.setAttribute("nome", "Projeto")
        child5.attributes.setAttribute("peso", "80")
        val doc = Document(root, 1.0, "UTF-8")

        @XMLTag("componente")
        class ComponenteAvaliacao(
            @AttributesAnnotation("nome")
            val nome: String,
            @AttributesAnnotation("peso")
            val peso: Int
        )

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("FUC")
            @AttributesAnnotation("FUC")
            val codigo: String,
            @XMLTag("nome")
            @Text("nome")
            val nome: String,
            @XMLTag("ects")
            @Text("ects")
            val ects: Double,
            @XMLTag("avaliacao")
            @NestedTags("avaliacao")
            val avaliacao: List<ComponenteAvaliacao>
        )

        val genericToSpecific = Translator(
            FUC(
                "M4310", "Programação Avançada",
                6.0,
                listOf(
                    ComponenteAvaliacao("Quizzes", 20),
                    ComponenteAvaliacao("Projeto", 80)
                )
            )
        ).createDoc()
        println(genericToSpecific.prettyPrint())
        assertEquals(doc.prettyPrint(), genericToSpecific.prettyPrint())
    }
```

##### '@NestedTags'

Placed above a variable that is a list of tags will make sure that children creation happens
This should be used for children of children. Direct children of the root of the document should solely be created using the XmlTag Annotation

###### EXAMPLE

        (...)
        @XMLTag("avaliacao")
        @NestedTags("avaliacao")
        val variable: List<*>
        (...)

        result:
        (...)
        <avaliacao>
            < 
            (...)
            />
        </avaliacao>
        (...)

```kotlin
    @Test
    fun createXmlStructureWithNestedTags() {
        val root = Tag("FUC")
        val child1 = Tag("avaliacaoola", root)
        Tag("componente", child1)
        Tag("componente", child1)
        val doc = Document( root, 1.0, "UTF-8")

        @XMLTag("componente")
        class ComponenteAvaliacao(
            @AttributesAnnotation("nome")
            val nome: String,
            @AttributesAnnotation("peso")
            val peso: Int
        )

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("avaliacao   ola")
            @NestedTags("avaliacao")
            val avaliacao: List<ComponenteAvaliacao>
        )

        val sortedTags = doc.getRootElement.getChildren.sortedBy { it.getTag }
        assertEquals(
            sortedTags[0].getTag, Translator(
                FUC(
                    listOf(
                        ComponenteAvaliacao("Quizzes", 20),
                        ComponenteAvaliacao("Projeto", 80)
                    )
                )
            ).createDoc().getRootElement.getChildren.sortedBy { it.getTag }[0].getTag
        )
    }
```

##### Translator class '@XMLString'

Used to alter the type or value stored in a string of an attribute

##### Translator class '@XMLAdapter'

Used to remove the values of the encapsulating tag of nested tags 

```kotlin
    @Test
    fun createTagWPostProcessing(){
        val root = Tag("FUC")
        Tag("nome", root)
        val child = Tag("ects", root)
        child.attributes.setAttribute("ects", "6.0%")
        val child3 = Tag("avaliacao", root)
        val child4 = Tag("componente", child3)
        child4.attributes.setAttribute("nome", "Quizzes")
        child4.attributes.setAttribute("peso", "20")
        val child5 = Tag("componente", child3)
        child5.attributes.setAttribute("nome", "Projeto")
        child5.attributes.setAttribute("peso", "80")
        child3.removeTag()
        val doc = Document(root, 1.0, "UTF-8")

        @XMLTag("componente")
        class ComponenteAvaliacao(
            @AttributesAnnotation("nome")
            val nome: String,
            @AttributesAnnotation("peso")
            val peso: Int
        )

        @RootTag
        @XMLTag("FUC")
        class FUC(
            @XMLTag("nome")
            val nome: String,
            @XMLTag("ects")
            @AttributesAnnotation("ects")
            @XMLString(AddPercentage::class)
            val ects: Double,
            @XMLTag("avaliacao")
            @NestedTags("avaliacao")
            @XMLAdapter(FUCAdapter::class)
            val avaliacao: List<ComponenteAvaliacao>
        )

        println(doc.prettyPrint())
        assertEquals(doc.prettyPrint(),
            Translator(FUC("filler", 6.0,
                listOf(
                    ComponenteAvaliacao("Quizzes", 20),
                    ComponenteAvaliacao("Projeto", 80)
                )))
                .createDoc().prettyPrint())
    }
```

##### DSL
        DSL was implemented for extra simplicity of the code it can be shown by the following tests

```kotlin
    @Test
    fun testinfixParentSetting(){
        val root = Tag("rootTag")
        val randomChild = Tag("childOfRoot")

        root fatherOf randomChild

        assertEquals(randomChild.getTag, root.getChildren[0].getTag)
    }

    @Test
    fun testLambdaTreeCreation(){
        val root = Tag("rootTag")
        val randomChild = Tag("childOfRoot", root)
        Tag("childOfchildOfRoot", randomChild)
        Tag("childOfchildOfRoot", randomChild)


        val nuRoot = Tag("rootTag").apply {
            childTag("childOfRoot"){
                childTag("childOfchildOfRoot"){}
                childTag("childOfchildOfRoot"){}
            }
        }

        val doc = Document(nuRoot, 1.0, "")

        println(doc.prettyPrint())
        assertEquals("childOfRoot", nuRoot.getChildren[0].getTag) // Ensure the first child of root is correct
        assertEquals("childOfchildOfRoot", nuRoot.getChildren[0].getChildren[0].getTag) // First child of "childOfRoot"
        assertEquals("childOfchildOfRoot", nuRoot.getChildren[0].getChildren[1].getTag)
    }

    @Test
    fun testFindTagsRecursively() {
        val root = Tag("rootTag").apply {
            childTag("firstchild") {
                childTag("subchild") {}
                childTag("subchild") {}
            }
            childTag("secondchild") {
                childTag("subchild") {}
            }
        }
        val tags = root / "subchild"
        assertEquals(3, tags.size)
        assertEquals("subchild", tags[0].getTag)
        assertEquals("subchild", tags[1].getTag)
        assertEquals("subchild", tags[2].getTag)
    }
```

## Code
https://github.com/JoGui2014/ProjetoFinalPA.git

Keep in mind all these test take into account all of the facets of the code therefore it is recommended one skips to the actual code to better understand the basic functionalities on display or just defer to the tutorials
