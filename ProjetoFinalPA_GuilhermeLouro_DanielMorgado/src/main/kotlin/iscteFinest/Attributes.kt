package iscteFinest
/**
 * Represents a collection of attributes associated with an XML tag.
 *
 * @property attributes The map storing attribute names and their corresponding values.
 * @constructor Creates a new Attributes instance with the specified attributes.
 */


class Attributes(private val attributes: MutableMap<String, String>) {

    /**
     * Checks if the provided string is a valid XML attribute name.
     *
     * @param input The string to check.
     * @return True if the string is a valid XML element name, false otherwise.
     */
    fun isValidXmlAttributeName(input: String): Boolean {
        val regex = "^[a-zA-Z_ ]+$".toRegex()
        return regex.matches(input)
    }

    init {
        attributes.forEach{
            if(!isValidXmlAttributeName(it.key) )
                throw IllegalArgumentException("Attribute names must be composed of regular characters such as letters or _ nothing else")
            if(it.key.isBlank() || it.value.isBlank())
                throw IllegalArgumentException("Attribute names and/or values cannot be empty or blank")
        }
    }

    /**
     * Retrieves the set of attribute names.
     */
    fun getAttribute(): Set<String> {
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
     * @param attributename The name of the attribute to set.
     * @param attribute The value to set for the attribute.
     */
    fun setAttribute(attributename: String, attribute: String) {
        var attributeName = attributename
        var attributeValue = attribute
        if(!isValidXmlAttributeName(attributeName))
            throw IllegalArgumentException("Attribute names must be composed of regular characters such as letters or _ nothing else")
        if(attributeName.isBlank() || attribute.isBlank())
            throw IllegalArgumentException("Attribute names and/or values cannot be empty or blank")
        attributeName = attributeName.replace(" ", "")
        attributeValue = attributeValue.replace(" ", "")
        attributes[attributeName] = attributeValue
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