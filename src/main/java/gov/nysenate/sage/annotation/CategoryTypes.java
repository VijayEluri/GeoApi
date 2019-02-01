package gov.nysenate.sage.annotation;

/**
 * This enum declares the types of Tests used in OpenLeg and their respective suffixes
 */
public enum CategoryTypes {
    IntegrationTest("IT"),
    UnitTest("Test"),
    SillyTest("Test");

    final String suffix;

    CategoryTypes(String suffix){
        this.suffix = suffix;
    }

    /**
     * Gets the suffix
     *
     * @return suffix
     */
    public String getSuffix(){
        return suffix;
    }
}