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