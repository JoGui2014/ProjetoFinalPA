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
                val attributeString = " $it" + """="${tag.attributes.getValues(it)}""""
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