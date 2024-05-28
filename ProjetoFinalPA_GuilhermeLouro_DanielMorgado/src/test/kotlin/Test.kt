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