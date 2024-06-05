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

#### Document Creation
```kotlin
Create an XML document by specifying the root tag, version, and encoding:
val document = Document(rootTag, 1.0, "UTF-8")
```

#### Pretty Print
```kotlin
Generate a formatted XML string:
val xmlString = document.prettyPrint()
println(xmlString)
```

#### Global Operations
Set, edit, and remove attributes and tags globally:
```kotlin
document.globalAttributeSetting("tag", "attribute", "value")
document.globalTagEditing("oldTag", "newTag")
document.globalTagRemoval("tag")
document.globalAttributeRemoval("tag", "attribute")
```

#### XPath Queries
Perform simple XPath queries:
```kotlin
val result = document.microXpath("root/child/tag")
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

##### Translator class '@XMLString'

Used to alter the type or value stored in a string of an attribute

##### Translator class '@XMLAdapter'

Used to remove the values of the encapsulating tag of nested tags 

## TestCases
Keep in mind all these test take into account all of the facets of the code therefore it is recommended one skips to the actual code to better understand the basic functionalities on display or just defer to the tutorials

```kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import iscteFinest.*

class Test {

    @Test
    fun createDocWithTag() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        assertEquals(listOf(cursoTag, cursoTag2), myDoc.getRootElement.getChildren)
    }

    @Test
    fun attributeTesting() {
        val planoTag = Tag("plano")
        planoTag.attributes.setAttribute("ano", "2023")
        planoTag.attributes.setAttribute("melhorquenoanopassado", "claroquenao")
        assertEquals(setOf("ano", "melhorquenoanopassado"), planoTag.attributes.getAttributes())
        planoTag.attributes.removeAttribute("melhorquenoanopassado")
        assertEquals(setOf("ano"), planoTag.attributes.getAttributes())
        planoTag.attributes.setAttribute("ano", "2024")
        assertEquals("2024", planoTag.attributes.getValues("ano"))
    }

    @Test
    fun parenthoodNavigation() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        val cadeiraTag1 = Tag("cadeira", cursoTag2)
        cadeiraTag1.attributes.setAttribute("ano", "2024")
        Document(planoTag, 1.0, "UTF-8")
        assertEquals(listOf(cursoTag, cursoTag2), planoTag.getChildren)
        assertEquals(emptyList<Tag>(), planoTag.getChildren[0].getChildren)
        assertEquals("2024", planoTag.getChildren[1].getChildren[0].attributes.getValues("ano"))
    }

    @Test
    fun testPrettyPrint() {
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
        assertEquals(xmlString, myDoc.prettyPrint())

    }

    @Test
    fun testGlobalAttributeInsertion() {
        val planoTag = Tag("plano")
        Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        myDoc.globalAttributeSetting("curso", "tipo", "diurno")
        val attributeMap = mutableMapOf<String, String>()
        attributeMap["tipo"] = "diurno"
        assertEquals(attributeMap.keys, myDoc.getRootElement.getChildren[0].attributes.getAttributes())
        assertEquals(attributeMap.keys, myDoc.getRootElement.getChildren[1].attributes.getAttributes())

    }

    @Test
    fun testGlobalTagEditing() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        myDoc.globalTagEditing("curso", "cadeira")
        assertEquals(listOf(cursoTag, cursoTag2), myDoc.getRootElement.getChildren)
    }

    @Test
    fun testGlobalTagRemoval() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        val cadeiraTag1 = Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        myDoc.globalTagRemoval("curso")
        assertEquals(null, cursoTag.getParent)
        assertEquals(listOf(cadeiraTag1), cursoTag2.getChildren)
    }

    @Test
    fun testGlobalAttributeRemoval() {
        val planoTag = Tag("plano")
        Tag("curso", planoTag)
        val cursoTag2 = Tag("curso", planoTag)
        Tag("cadeira", cursoTag2)
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        myDoc.globalAttributeSetting("curso", "tipo", "diurno")
        myDoc.globalAttributeRemoval("curso", "tipo")
        assertEquals(emptySet<String>(), myDoc.getRootElement.getChildren[0].attributes.getAttributes())
    }

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
        componenteTag1.attributes.setAttribute("nome", "Quizzes")
        componenteTag1.attributes.setAttribute("peso", "20%")
        val componenteTag2 = Tag("componente", avaliacaoTag)
        componenteTag2.attributes.setAttribute("nome", "Projeto")
        componenteTag2.attributes.setAttribute("peso", "80%")
        val myDoc = Document(planoTag, 1.0, "UTF-8")
        assertEquals(
            listOf("""<componente nome="Quizzes" peso="20%"/>""", """<componente nome="Projeto" peso="80%"/>"""),
            myDoc.microXpath("plano/fuc/avaliacao/componente")
        )
    }

    @Test
    fun testTagpath() {
        val planoTag = Tag("plano")
        val cursoTag = Tag("curso", planoTag)
        Tag("curso", planoTag)
        val cadeiraTag6 = Tag("cadeira", cursoTag)
        Document(planoTag, 1.0, "UTF-8")
        assertEquals("plano/curso/cadeira", cadeiraTag6.getPath)
    }

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

    @Test
    fun createXmlStructureWithNestedTags() {
        val root = Tag("FUC")
        val child1 = Tag("avaliacao", root)
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
            @XMLTag("avaliacao")
            @NestedTags
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

    @Test
    fun testSimpleXMLStructureFullWithPrettyPrint() {
        val root = Tag("FUC")
        Tag("nome", root)
        Tag("ects", root)
        val child1 = Tag("avaliacao", root)
        val child4 = Tag("componente", child1)
        child4.attributes.setAttribute("nome", "Quizzes")
        child4.attributes.setAttribute("peso", "20")
        val child5 = Tag("componente", child1)
        child5.attributes.setAttribute("nome", "Projeto")
        child5.attributes.setAttribute(
            "peso", "80"
        )
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
            val ects: Double,
            @XMLTag("avaliacao")
            @NestedTags
            val avaliacao: List<ComponenteAvaliacao>
        )

        val genericToSpecific = Translator(
            FUC(
                "nome", 1.0, listOf(
                    ComponenteAvaliacao("Quizzes", 20),
                    ComponenteAvaliacao("Projeto", 80)
                )
            )
        ).createDoc()
        assertEquals(doc.prettyPrint(), genericToSpecific.prettyPrint())
    }

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
            @AttributesAnnotation("codigo")
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

    @Test
    fun testFullXmlCreationWithAllTheFixins() {
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
            @AttributesAnnotation("codigo")
            val codigo: String,
            @XMLTag("nome")
            @Text
            val nome: String,
            @XMLTag("ects")
            @Text
            val ects: Double,
            @XMLTag("avaliacao")
            @NestedTags
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

    @Test
    fun createTagWAnnotations(){
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

        assertEquals(doc.getRootElement.getTag, Translator(FUC("filler", 0.0)).createTag().getTag)
    }

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
            @NestedTags
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

    //DSL
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
            childTag("child1") {
                childTag("subchild") {}
                childTag("subchild") {}
            }
            childTag("child2") {
                childTag("subchild") {}
            }
        }
        val tags = root / "subchild"
        assertEquals(3, tags.size)
        assertEquals("subchild", tags[0].getTag)
        assertEquals("subchild", tags[1].getTag)
        assertEquals("subchild", tags[2].getTag)
    }
}

```

## Code

```kotlin

package iscteFinest
/**
 * Represents a document with a root tag and provides methods for manipulating tags and attributes globally.
 *
 * @property rootTag The root tag of the document.
 * @property version The version of the document.
 * @property encoding The encoding of the document.
 */
data class Document(
    private val rootTag: Tag,
    private val version: Double,
    private val encoding: String
) {

    /**
     * Retrieves the root element of the document.
     */
    val getRootElement: Tag
        get() = rootTag

    /**
     * Returns a string representation of the document in a pretty-printed XML format.
     * The document version and encoding are included in the XML declaration.
     *
     * @return The pretty-printed XML string.
     */
    fun prettyPrint(): String {
        val begin = """<?xml version="${this.version}" encoding="${this.encoding}"?>"""
        return begin + "\n" + prettyPrintSegment(this.rootTag)
    }

    /**
     * Generates a pretty-printed representation of the XML segment starting from the given tag.
     * Includes attributes, text content, and nested tags with appropriate indentation.
     *
     * @param tag The tag representing the XML segment to pretty-print.
     * @return The pretty-printed XML segment as a string.
     */
    private fun prettyPrintSegment(tag: Tag): String {
        var content = ""
        val startString = prettyAttributes(tag) + ">"
        content += startString + "\n"
        val endname = "</" + tag.getTag + ">"

        val children = tag.getChildren
        children.forEach {
            content += if (it.getChildren.isEmpty()) {
                addTabsForParents(it) + prettyPrintLine(it) + "\n"
            } else {
                addTabsForParents(it) + prettyPrintSegment(it) + "\n"
            }
        }
        content += addTabsForParents(tag) + endname
        return content

    }

    /**
     * Generates a pretty-printed representation of a single tag line,
     * including attributes and text content if present.
     *
     * @param tag The tag to pretty-print.
     * @return The pretty-printed tag line as a string.
     */
    private fun prettyPrintLine(tag: Tag): String {
        val fullString: String
        if (tag.getText.isNullOrEmpty()) {
            val startString = prettyAttributes(tag) + "/>"
            fullString = startString
        } else {
            val startString = prettyAttributes(tag) + ">"
            val endname = "</" + tag.getTag + ">"
            fullString = startString + "${tag.getText}" + endname
        }

        return fullString
    }

    /**
     * Generates a string representation of the attributes of a tag in XML format.
     *
     * @param tag The tag whose attributes to represent.
     * @return The attributes of the tag as a string.
     */
    private fun prettyAttributes(tag: Tag): String {
        var startname = "<" + tag.getTag
        if(tag.attributes.getAttributes().isNotEmpty()) {
            val attributes = tag.attributes.getAttributes()
            attributes.forEach {
                val attributeString = " $it" + """="${tag.attributes.getValues(it)}" """
                startname += attributeString
            }
        }

        return startname
    }

    /**
     * Generates indentation string based on the depth of the tag in the XML structure.
     *
     * @param tag The tag whose depth determines the indentation.
     * @return The indentation string.
     */
    private fun addTabsForParents(tag: Tag): String {
        val aux = tag.getDepth
        var string = ""
        for (i in 1..aux) {
            string += "\t"
        }

        return string
    }

    /**
     * Sets an attribute globally for all tags with a specified tag name.
     *
     * @param tagName The name of the tags to apply the attribute to.
     * @param attributeName The name of the attribute to set.
     * @param attributeValue The value of the attribute to set.
     */
    fun globalAttributeSetting(tagName: String, attributeName: String, attributeValue: String) {
        rootTag.accept {
            if (it.getTag == tagName)
                it.attributes.setAttribute(attributeName, attributeValue)

            true
        }
    }

    /**
     * Edits the tag name globally for all tags with a specified current tag name.
     *
     * @param currentTagName The current name of the tags to be edited.
     * @param newName The new name to set for the tags.
     */
    fun globalTagEditing(currentTagName: String, newName: String) {
        rootTag.accept {
            if (it.getTag == currentTagName)
                it.setTag(newName)
            true
        }
    }

    /**
     * Removes tags globally with a specified tag name.
     *
     * @param tagToRemove The name of the tags to remove.
     */
    fun globalTagRemoval(tagToRemove: String) {
        val removalSubjects: MutableList<Tag> = mutableListOf()
        rootTag.accept {
            if (it.getTag == tagToRemove) {
                removalSubjects.add(it)
            }
            true
        }
        removalSubjects.forEach {
            it.removeTag()
            it.getParent
        }
    }

    /**
     * Removes attributes globally for all tags with a specified tag name.
     *
     * @param tagName The name of the tags to remove the attribute from.
     * @param attributeName The name of the attribute to remove.
     */
    fun globalAttributeRemoval(tagName: String, attributeName: String) {
        rootTag.accept {
            if (it.getTag == tagName)
                it.attributes.removeAttribute(attributeName)
            true
        }
    }

    /**
     * Executes a micro XPath query in the form of Tag/Tag/Tag on the document and returns a list of strings representing the found elements.
     *
     * @param xPath The micro XPath query to execute.
     * @return A list of strings representing the found elements.
     */
    fun microXpath(xPath: String): List<String> {
        val stringList: MutableList<String> = mutableListOf()
        val tagList = mutableListOf<Tag>()
        try {
            if ("/" !in xPath) {
                throw IllegalArgumentException("Invalid XPath string: $xPath. Must contain '/'")
            }
            tagList.addAll(findTagsInPath(xPath))
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
        tagList.forEach {
            stringList.add(this.prettyPrintLine(it))
        }
        return stringList
    }

    /**
     * Finds tags in the document that match the given XPath query and returns them as a list.
     *
     * @param xPath The XPath query to execute.
     * @return A list of tags matching the XPath query.
     */
    private fun findTagsInPath(xPath: String): List<Tag> {
        val tagList: MutableList<Tag> = mutableListOf()
        val xPathArrayForm = xPath.split("/")
        if (xPathArrayForm.first() != getRootElement.getTag) {
            return emptyList()
        }
        val objectiveTag = xPathArrayForm.last()
        findWithTag(objectiveTag, getRootElement, tagList)
        tagList.removeIf { it.getPath != xPath }
        return tagList
    }

    /**
     * Recursively finds tags with the specified tag name within the subtree rooted at the given current tag.
     *
     * @param tagName The name of the tag to search for.
     * @param currentTag The current tag being examined.
     * @param tagList The list to which the found tags are added.
     * @return The updated list of tags containing the found tags.
     */
    private fun findWithTag(tagName: String, currentTag: Tag, tagList: MutableList<Tag>): MutableList<Tag> {
        currentTag.getChildren.forEach {
            if (it.getTag == tagName) {
                tagList.add(it)
            } else
                findWithTag(tagName, it, tagList)
        }
        return tagList
    }

}


package iscteFinest

/**
 * Annotation to define the XML tag name.
 *
 * @property tagName The name of the XML tag.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XMLTag(val tagName: String)

/**
 * Annotation to mark the root tag.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class RootTag

/**
 * Annotation to define nested tags.
 *
 * @property associatedTag The name of the associated tag.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class NestedTags(val associatedTag: String)

/**
 * Annotation to define XML attributes.
 *
 * @property associatedTag The name of the associated tag.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class AttributesAnnotation(val associatedTag: String)

/**
 * Annotation to define text content.
 *
 * @property associatedTag The name of the associated tag.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class Text(val associatedTag: String)

/**
 * Class representing an XML tag with attributes and nested tags.
 *
 * @property tagName The name of the XML tag.
 * @property parent The parent tag.
 * @property text The text content of the tag.
 */
class Tag(
    private var tagName: String,
    private var parent: Tag? = null,
    private var text: String? = null
) {
    init {
        if (tagName.isBlank()) {
            throw IllegalArgumentException("Tag name cannot be empty or blank")
        }
        parent?.children?.add(this)
    }

    val attributes: Attributes = Attributes(mutableMapOf())
    private val children: MutableList<Tag> = mutableListOf()

    /**
     * Creates a child tag.
     *
     * @param name The name of the child tag.
     * @param text The text content of the child tag.
     * @param attributes The attributes of the child tag.
     * @param init Initialization block for the child tag.
     * @return The created child tag.
     */
    fun childTag(name: String, text: String? = null, attributes: MutableMap<String, String>? = null, init: Tag.() -> Unit = {}): Tag =
        Tag(name, this).apply {
            if (!text.isNullOrEmpty()) {
                this.setText(text)
            }
            if (!attributes.isNullOrEmpty()) {
                attributes.forEach {
                    this.attributes.setAttribute(it.key, it.value)
                }
            }
            init(this)
        }

    /**
     * Accepts a visitor function to traverse the tag and its children.
     *
     * @param visitor The visitor function.
     */
    fun accept(visitor: (Tag) -> Boolean) {
        if (visitor(this)) {
            children.forEach {
                it.accept(visitor)
            }
        }
    }

    val getTag: String
        get() = this.tagName

    /**
     * Sets a new tag name.
     *
     * @param newTag The new tag name.
     */
    fun setTag(newTag: String) {
        if (newTag.isBlank()) {
            throw IllegalArgumentException("Tag name cannot be empty or blank")
        }
        this.tagName = newTag
    }

    val getParent: Tag?
        get() = parent

    /**
     * Sets the parent tag for the current tag and its children.
     *
     * @param newParent The new parent tag.
     */
    private fun setParent(newParent: Tag) {
        children.forEach {
            it.parent = newParent
            this.parent!!.setChild(it)
        }
    }

    val getText: String?
        get() = text

    /**
     * Sets the text content.
     *
     * @param text The text content.
     * @throws IllegalStateException If there are child elements present.
     */
    fun setText(text: String) {
        if (this.getChildren.isEmpty())
            this.text = text
        else
            throw IllegalStateException("Cannot set text when there are child elements present")
    }

    val getChildren: List<Tag>
        get() = children

    /**
     * Removes a child tag.
     *
     * @param tagToRemove The child tag to remove.
     */
    private fun removeChild(tagToRemove: Tag) {
        this.children.remove(tagToRemove)
    }

    /**
     * Adds a child tag.
     *
     * @param tagToAdd The child tag to add.
     */
    private fun setChild(tagToAdd: Tag) {
        this.children.add(tagToAdd)
    }

    /**
     * Removes the tag. Making sure that whatever children it has get their parent set to the deleted tags parent.
     */
    fun removeTag() {
        if (this.getParent != null) {
            this.setParent(this.parent!!)
            this.parent!!.removeChild(this)
        }
        this.tagName = ""
        this.parent = null
    }

    val getDepth: Int
        get() = if (getParent != null) 1 + getParent!!.getDepth else 0

    val getPath: String
        get() = if (getParent != null) "${getParent!!.getPath}/${getTag}" else getTag

    /**
     * Sets the current tag as the parent of the given child tag.
     *
     * @param childTag The child tag.
     */
    infix fun fatherOf(childTag: Tag) {
        if (childTag.getParent != null) {
            childTag.parent!!.children.remove(childTag)
        }
        childTag.parent = this
        this.children.add(childTag)
    }

    /**
     * Finds tags recursively by their name.
     *
     * @param name The name of the tags to find.
     * @param result The list to store found tags.
     * @return The list of found tags.
     */
    private fun findTagsRecursively(name: String, result: MutableList<Tag> = mutableListOf()): List<Tag> {
        children.forEach {
            if (it.getTag == name) {
                result.add(it)
            } else {
                it.findTagsRecursively(name, result)
            }
        }
        return result
    }

    /**
     * Finds tags recursively by their name.
     *
     * @param name The name of the tags to find.
     * @return The list of found tags.
     */
    operator fun div(name: String): List<Tag> {
        return findTagsRecursively(name)
    }
}



package iscteFinest
/**
 * Represents a collection of attributes associated with an XML tag.
 *
 * @property attributes The map storing attribute names and their corresponding values.
 * @constructor Creates a new Attributes instance with the specified attributes.
 */


class Attributes(private val attributes: MutableMap<String, String>) {

    init {
        attributes.forEach{
            if(it.key.isBlank() || it.value.isBlank())
                throw IllegalArgumentException("Attribute names and/or values cannot be empty or blank")
        }
    }

    /**
     * Retrieves the set of attribute names.
     */
    fun getAttributes(): Set<String> {
        return attributes.keys
    }

    /**
     * Retrieves the value of the attribute with the specified name.
     *
     * @param name The name of the attribute to retrieve.
     * @return The value of the attribute, or null if the attribute does not exist.
     */
    fun getValues(name: String): String? {
        return attributes[name]
    }

    /**
     * Sets the value of the specified attribute.
     *
     * @param attributeName The name of the attribute to set.
     * @param attribute The value to set for the attribute.
     */
    fun setAttribute(attributeName: String, attribute: String) {
        attributes[attributeName] = attribute
    }

    /**
     * Removes the specified attribute.
     *
     * @param attributeName The name of the attribute to remove.
     */
    fun removeAttribute(attributeName: String) {
        attributes.remove(attributeName)
    }
}


package iscteFinest

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Annotation for string transformation.
 *
 * @property transformer The transformer class.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLString(val transformer: KClass<out Transformer>)

/**
 * Annotation for XML adaptation.
 *
 * @property transformer The adapter class.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class XMLAdapter(val transformer: KClass<out Adapter>)

/**
 * Interface for transforming strings.
 */
interface Transformer {
    fun transform(value: String): String
}

/**
 * Interface for adapting XML elements.
 */
interface Adapter {
    fun adapt(xmlElement: Tag)
}

/**
 * Transformer that adds a percentage symbol when the XMLAdapter annotation is present.
 */
class AddPercentage : Transformer {
    override fun transform(value: String): String {
        return "$value%"
    }
}

/**
 * Adapter that removes a tag whenever the annotation XMLString is present.
 */
class FUCAdapter : Adapter {
    override fun adapt(xmlElement: Tag) {
        xmlElement.removeTag()
    }
}

/**
 * Class responsible for translating an object into an XML document or tag structure.
 *
 * @property genericObject The object to be translated.
 */
class Translator(private val genericObject: Any) {

    /**
     * Creates an XML document from the object.
     *
     * @return The created XML document.
     */
    fun createDoc(): Document {
        return Document(
            createParentElement(genericObject),
            1.0,
            "UTF-8"
        )
    }

    /**
     * Creates an XML tag from the object.
     *
     * @return The created XML tag.
     */
    fun createTag(): Tag {
        return createParentElement(genericObject)
    }

    /**
     * Creates the parent XML element for the given object.
     *
     * @param genericClass The object for which the XML element is created.
     * @param fatherTag The parent tag, if any.
     * @return The created parent XML tag.
     * @throws IllegalStateException If no root tag is found or if the tag is empty.
     */
    private fun createParentElement(genericClass: Any, fatherTag: Tag? = null): Tag {
        val tagClass = genericClass::class
        val rootTag = if (fatherTag == null)
            if (tagClass.hasAnnotation<XMLTag>())
                if (tagClass.hasAnnotation<RootTag>())
                    Tag(tagClass.findAnnotation<XMLTag>()!!.tagName)
                else {
                    val rootTagName = tagClass.declaredMemberProperties
                        .find { it.hasAnnotation<RootTag>() && it.hasAnnotation<XMLTag>() }
                        ?.findAnnotation<XMLTag>()?.tagName
                    if (rootTagName != null)
                        Tag(rootTagName)
                    else
                        throw IllegalStateException("No rootTag annotation found there must be at least one")
                }
            else
                throw IllegalStateException("No rootTag with XMLTag annotation found")
        else
            Tag(tagClass.findAnnotation<XMLTag>()!!.tagName, fatherTag)
        genericClass.findAttributes(rootTag)
        if (rootTag.getTag.isBlank()) throw IllegalStateException("No Tag should be empty")
        createChildrenTags(rootTag, genericClass)
        return rootTag
    }

    /**
     * Creates child tags for the given root tag and object.
     *
     * @param rootTag The parent tag.
     * @param genericClass The object for which the child tags are created.
     */
    private fun createChildrenTags(rootTag: Tag, genericClass: Any) {
        val tagClass = genericClass::class
        tagClass.declaredMemberProperties
            .filter {
                it.hasAnnotation<XMLTag>()
                        && it.findAnnotation<XMLTag>()!!.tagName != rootTag.getTag
            }
            .reversed()
            .forEach { listProperty ->
                genericClass.childrenCreationSteps(listProperty, rootTag)
            }
    }

    /**
     * Creates steps for the creation of child elements for a given property and root tag.
     *
     * @param listProperty The property for which the child elements are created.
     * @param rootTag The parent tag.
     */
    private fun Any.childrenCreationSteps(listProperty: KProperty<*>, rootTag: Tag) {
        if (listProperty.findAnnotation<XMLTag>()!!.tagName.isBlank())
            throw IllegalArgumentException("Tag name in XMLTag annotation cannot be empty or blank")
        // Creation of the new tag
        val newTag = Tag(listProperty.findAnnotation<XMLTag>()!!.tagName, rootTag)
        // Creation of attributes
        this.findAttributes(newTag)
        // Creation of text or creation of children
        val willHaveNestedTags = this.createWithText(newTag, listProperty)
        if (willHaveNestedTags)
            this.createNestedTags(newTag)
        // Application of a preprocessing effect such as adaptation
        listProperty.findAnnotation<XMLAdapter>()?.let {
            val transformer = it.transformer.constructors.first().call()
            transformer.adapt(newTag)
        }
    }

    /**
     * Creates nested tags for a given parent tag.
     *
     * @param fatherTag The parent tag.
     */
    private fun Any.createNestedTags(fatherTag: Tag) {
        val tagClass = this::class
        tagClass.declaredMemberProperties.filter {
            it.hasAnnotation<NestedTags>()
                    && it.findAnnotation<NestedTags>()!!.associatedTag == fatherTag.getTag
                    && it.getter.call(this) is List<*>
        }
            .forEach {
                (it.getter.call(this) as List<*>).forEach { nestedTag ->
                    if (nestedTag != null) {
                        createParentElement(nestedTag, fatherTag)
                    }
                }
            }
    }

    /**
     * Finds and sets attributes for a given tag.
     *
     * @param tagWithAttributes The tag to set attributes for.
     */
    private fun Any.findAttributes(tagWithAttributes: Tag) {
        val tagClass = this::class
        tagClass.declaredMemberProperties.filter {
            !it.hasAnnotation<XMLTag>()
                    || (it.hasAnnotation<AttributesAnnotation>()
                    && it.findAnnotation<AttributesAnnotation>()!!.associatedTag == tagWithAttributes.getTag)
        }
            .forEach {
                this.createWithAttributes(tagWithAttributes, it)
            }
    }

    /**
     * Creates attributes for a given tag based on the property.
     *
     * @param tagWithAttributes The tag to set attributes for.
     * @param property The property containing attribute data.
     */
    private fun Any.createWithAttributes(tagWithAttributes: Tag, property: KProperty<*>) {
        val attributeValue = property.getter.call(this).toString()
        val transformedValue = property.findAnnotation<XMLString>()?.let { annotation ->
            val transformer = annotation.transformer.constructors.first().call()
            transformer.transform(attributeValue)
        } ?: attributeValue
        tagWithAttributes.attributes.setAttribute(
            property.name,
            transformedValue
        )
    }

    /**
     * Creates text content for a given tag based on the property.
     *
     * @param tagWithText The tag to set text for.
     * @param property The property containing text data.
     * @return `true` if nested tags will be created, `false` otherwise.
     */
    private fun Any.createWithText(tagWithText: Tag, property: KProperty<*>): Boolean {
        this::class.declaredMemberProperties
            .filter { it.hasAnnotation<Text>() && it.findAnnotation<Text>()?.associatedTag == tagWithText.getTag }
            .forEach {
                tagWithText.setText(property.getter.call(this).toString())
                return false
            }
        return true
    }
}
```

