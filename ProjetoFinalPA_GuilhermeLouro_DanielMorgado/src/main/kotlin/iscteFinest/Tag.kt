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
        if(!isValidXmlElementName(tagName))
            throw IllegalArgumentException("The tag name must only contain normal characters such as letters or underscores")
        tagName = tagName.replace(" ", "")
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
