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
        genericClass.findAttributes(rootTag, rootTag.getTag)
        if (rootTag.getTag.isBlank()) throw IllegalStateException("No Tag should be empty")
        createChildrenTags(rootTag, genericClass)
        return rootTag
    }

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

    private fun Any.childrenCreationSteps(listProperty: KProperty<*>, rootTag: Tag) {
        if (listProperty.findAnnotation<XMLTag>()!!.tagName.isBlank())
            throw IllegalArgumentException("Tag name in XMLTag annotation cannot be empty or blank")
        // Creation of the new tag
        val newTag = Tag(listProperty.findAnnotation<XMLTag>()!!.tagName, rootTag)
        // Creation of attributes
        if (listProperty.hasAnnotation<AttributesAnnotation>())
            this.findAttributes(
                newTag,
                listProperty.findAnnotation<XMLTag>()!!.tagName
            )
        // Creation of text or creation of children
        if (listProperty.hasAnnotation<Text>()) this.createWithText(newTag, listProperty)
        else if (listProperty.hasAnnotation<NestedTags>())
            if(listProperty.getter.call(this) is List<*>) // Making sure that it is in fact a list
                createNestedTags(newTag, listProperty.getter.call(this) as List<*>)
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
     * @param listOfTags The list of nested tags to create.
     */
    private fun createNestedTags(fatherTag: Tag, listOfTags: List<*>) {
        listOfTags.forEach { nestedTag ->
            if (nestedTag != null) {
                createParentElement(nestedTag, fatherTag)
            }
        }
    }

    /**
     * Finds and sets attributes for a given tag.
     *
     * @param tagWithAttributes The tag to set attributes for.
     * @param xmlTag The XML tag name.
     */
    private fun Any.findAttributes(tagWithAttributes: Tag, xmlTag: String) {
        val tagClass = this::class
        tagClass.declaredMemberProperties.filter { it.hasAnnotation<AttributesAnnotation>() }
            .forEach {
                if (!it.hasAnnotation<XMLTag>()
                    || it.findAnnotation<XMLTag>()!!.tagName == xmlTag
                ) {
                    this.createWithAttributes(tagWithAttributes, it)
                }
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
            property.findAnnotation<AttributesAnnotation>()!!.attributeName,
            transformedValue
        )
    }

    /**
     * Creates text content for a given tag based on the property.
     *
     * @param tagWithText The tag to set text for.
     * @param property The property containing text data.
     */
    private fun Any.createWithText(tagWithText: Tag, property: KProperty<*>) {
        if (property.hasAnnotation<Text>())
            tagWithText.setText(property.getter.call(this).toString())
    }
}