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
     * Checks if the provided string is a valid XML tag name.
     *
     * @param input The string to check.
     * @return True if the string is a valid XML element name, false otherwise.
     */
    fun isValidXmlElementName(input: String): Boolean {
        val regex = "^[a-zA-Z_ ]+$".toRegex()
        return regex.matches(input)
    }

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
                if (tagClass.hasAnnotation<RootTag>()) {
                    val tagName = tagClass.findAnnotation<XMLTag>()!!.tagName.replace(" ", "")
                    if(!isValidXmlElementName(tagName))
                        throw IllegalArgumentException("The tag name must only contain normal characters such as letters or underscores")
                    Tag(tagName)
                }else {
                    val rootTagName = tagClass.declaredMemberProperties
                        .find { it.hasAnnotation<RootTag>() && it.hasAnnotation<XMLTag>() }
                        ?.findAnnotation<XMLTag>()?.tagName
                    if (rootTagName != null) {
                        val tagName = rootTagName.replace(" ", "")
                        if(!isValidXmlElementName(tagName))
                            throw IllegalArgumentException("The tag name must only contain normal characters such as letters or underscores")
                        Tag(tagName)
                    }else
                        throw IllegalStateException("No rootTag annotation found there must be at least one")
                }
            else
                throw IllegalStateException("No rootTag with XMLTag annotation found")
        else {
            val tagName = tagClass.findAnnotation<XMLTag>()!!.tagName.replace(" ", "")
            if(!isValidXmlElementName(tagName))
                throw IllegalArgumentException("The tag name must only contain normal characters such as letters or underscores")
            Tag(tagName, fatherTag)
        }
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
        val tagName = listProperty.findAnnotation<XMLTag>()!!.tagName.replace(" ", "")
        if(!isValidXmlElementName(tagName))
            throw IllegalArgumentException("The tag name must only contain normal characters such as letters or underscores")
        // Creation of the new tag
        val newTag = Tag(tagName, rootTag)
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
