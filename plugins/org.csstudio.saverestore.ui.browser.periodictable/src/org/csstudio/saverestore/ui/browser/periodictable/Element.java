/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui.browser.periodictable;

/**
 *
 * <code>Element</code> provides the list of all elements in the periodic table, together with their basic properties,
 * such as atomic number, most common isotope and most common charge.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum Element {
    H("H","Hydrogen",AggregationState.GAS,ElementType.DIATOMIC_NONMETAL,1,1,0,1), HE("He","Helium",AggregationState.GAS,ElementType.NOBLE_GAS,2,18,2,0),
    LI("Li","Lithium",AggregationState.SOLID,ElementType.ALKALI_METAL,3,1,4,1), BE("Be","Beryllium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,4,2,5,2),
        B("B","Boron",AggregationState.SOLID,ElementType.METALLOID,5,13,6,3), C("C","Carbon",AggregationState.SOLID,ElementType.POLYATOMIC_NONMETAL,6,14,6,4),
        N("N","Nitrogen",AggregationState.GAS,ElementType.DIATOMIC_NONMETAL,7,15,7,-2), O("O","Oxygen",AggregationState.GAS,ElementType.DIATOMIC_NONMETAL,8,16,8,-2),
        F("F","Fluorine",AggregationState.GAS,ElementType.DIATOMIC_NONMETAL,9,17,10,-1), NE("Ne","Neon",AggregationState.GAS,ElementType.NOBLE_GAS,10,18,10,0),
    NA("Na","Sodium",AggregationState.SOLID,ElementType.ALKALI_METAL,11,1,12,1), MG("Mg","Magnesium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,12,2,12,2),
        AL("Al","Aluminium",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,13,13,14,3), SI("Si","Silicon",AggregationState.SOLID,ElementType.METALLOID,14,14,14,4),
        P("P","Phosphorus",AggregationState.SOLID,ElementType.POLYATOMIC_NONMETAL,15,15,16,5), S("S","Sulfur",AggregationState.SOLID,ElementType.POLYATOMIC_NONMETAL,16,16,16,-2),
        CL("Cl","Chlorine",AggregationState.GAS,ElementType.DIATOMIC_NONMETAL,17,17,18,-1), AR("Ar","Argon",AggregationState.GAS,ElementType.NOBLE_GAS,18,18,22,0),
    K("K","Potassium",AggregationState.SOLID,ElementType.ALKALI_METAL,19,1,20,1), CA("Ca","Calcium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,20,2,20,2),
        SC("Sc","Scandium",AggregationState.SOLID,ElementType.TRANSITION_METAL,21,3,24,3), TI("Ti","Titanium",AggregationState.SOLID,ElementType.TRANSITION_METAL,22,4,46,4),
        V("V","Vanadium",AggregationState.SOLID,ElementType.TRANSITION_METAL,23,5,28,3), CR("Cr","Chromium",AggregationState.SOLID,ElementType.TRANSITION_METAL,24,6,28,2),
        MN("Mn","Manganese",AggregationState.SOLID,ElementType.TRANSITION_METAL,25,7,30,2), FE("Fe","Iron",AggregationState.SOLID,ElementType.TRANSITION_METAL,26,8,30,2),
        CO("Co","Cobalt",AggregationState.SOLID,ElementType.TRANSITION_METAL,27,9,32,2), NI("Ni","Nickel",AggregationState.SOLID,ElementType.TRANSITION_METAL,28,10,31,2),
        CU("Cu","Copper",AggregationState.SOLID,ElementType.TRANSITION_METAL,29,11,35,2), ZN("Zn","Zinc",AggregationState.SOLID,ElementType.TRANSITION_METAL,30,12,35,2),
        GA("Ga","Gallium",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,31,13,39,3), GE("Ge","Germanium",AggregationState.SOLID,ElementType.METALLOID,32,14,41,4),
        AS("As","Arsenic",AggregationState.SOLID,ElementType.METALLOID,33,15,42,5), SE("Se","Selenium",AggregationState.SOLID,ElementType.POLYATOMIC_NONMETAL,34,16,45,-2),
        BR("Br","Bromine",AggregationState.LIQUID,ElementType.DIATOMIC_NONMETAL,35,17,45,-1), KR("Kr","Krypton",AggregationState.GAS,ElementType.NOBLE_GAS,36,18,48,0),
    RB("Rb","Rubidium",AggregationState.SOLID,ElementType.ALKALI_METAL,37,1,48,1), SR("Sr","Strontium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,38,2,50,2),
        Y("Y","Yttrium",AggregationState.SOLID,ElementType.TRANSITION_METAL,39,3,50,3), ZR("Zr","Zirconium",AggregationState.SOLID,ElementType.TRANSITION_METAL,40,4,51,4),
        NB("Nb","Niobium",AggregationState.SOLID,ElementType.TRANSITION_METAL,41,5,52,5), MO("Mo","Molybdenum",AggregationState.SOLID,ElementType.TRANSITION_METAL,42,6,54,4),
        TC("Tc","Technetium",AggregationState.SOLID,ElementType.TRANSITION_METAL,43,7,56,4), RU("Ru","Ruthenium",AggregationState.SOLID,ElementType.TRANSITION_METAL,44,8,57,3),
        RH("Rh","Rhodium",AggregationState.SOLID,ElementType.TRANSITION_METAL,45,9,58,3), PD("Pd","Palladium",AggregationState.SOLID,ElementType.TRANSITION_METAL,46,10,60,2),
        AG("Ag","Silver",AggregationState.SOLID,ElementType.TRANSITION_METAL,47,11,61,1), CD("Cd","Cadmium",AggregationState.SOLID,ElementType.TRANSITION_METAL,48,12,64,2),
        IN("In","Indium",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,49,13,66,3), SN("Sn","Tin",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,50,14,69,-4),
        SB("Sb","Antimony",AggregationState.SOLID,ElementType.METALLOID,51,15,71,3), TE("Te","Tellurium",AggregationState.SOLID,ElementType.METALLOID,52,16,76,4),
        I("I","Iodine",AggregationState.SOLID,ElementType.DIATOMIC_NONMETAL,53,17,74,-1), XE("Xe","Xenon",AggregationState.GAS,ElementType.NOBLE_GAS,54,18,77,0),
    CS("Cs","Caesium",AggregationState.SOLID,ElementType.ALKALI_METAL,55,1,78,1), BA("Ba","Barium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,56,2,81,2),
    LA("La","Lanthanum",AggregationState.SOLID,ElementType.LANTHANIDE,57,0,82,3), CE("Ce","Cerium",AggregationState.SOLID,ElementType.LANTHANIDE,58,0,82,3),
        PR("Pr","Praseodymium",AggregationState.SOLID,ElementType.LANTHANIDE,59,0,82,3), ND("Nd","Neodymium",AggregationState.SOLID,ElementType.LANTHANIDE,60,0,84,3),
        PM("Pm","Promethium",AggregationState.SOLID,ElementType.LANTHANIDE,61,0,84,3), SM("Sm","Samarium",AggregationState.SOLID,ElementType.LANTHANIDE,62,0,88,3),
        EU("Eu","Europium",AggregationState.SOLID,ElementType.LANTHANIDE,63,0,89,3), GD("Gd","Gadolinium",AggregationState.SOLID,ElementType.LANTHANIDE,64,0,93,3),
        TB("Tb","Terbium",AggregationState.SOLID,ElementType.LANTHANIDE,65,0,94,3), DY("Dy","Dysprosium",AggregationState.SOLID,ElementType.LANTHANIDE,66,0,97,3),
        HO("Ho","Holmium",AggregationState.SOLID,ElementType.LANTHANIDE,67,0,98,3), ER("Er","Erbium",AggregationState.SOLID,ElementType.LANTHANIDE,68,0,99,3),
        TM("Tm","Thulium",AggregationState.SOLID,ElementType.LANTHANIDE,69,0,100,3), YB("Yb","Ytterbium",AggregationState.SOLID,ElementType.LANTHANIDE,70,0,103,3),
        LU("Lu","Lutetium",AggregationState.SOLID,ElementType.LANTHANIDE,71,0,104,3),
    HF("Hf","Hafnium",AggregationState.SOLID,ElementType.TRANSITION_METAL,72,4,106,4), TA("Ta","Tantalum",AggregationState.SOLID,ElementType.TRANSITION_METAL,73,5,108,5),
        W("W","Tungsten",AggregationState.SOLID,ElementType.TRANSITION_METAL,74,6,110,4), RE("Re","Rhenium",AggregationState.SOLID,ElementType.TRANSITION_METAL,75,7,111,4),
        OS("Os","Osmium",AggregationState.SOLID,ElementType.TRANSITION_METAL,76,8,114,4), IR("Ir","Iridium",AggregationState.SOLID,ElementType.TRANSITION_METAL,77,9,115,4),
        PT("Pt","Platinum",AggregationState.SOLID,ElementType.TRANSITION_METAL,78,10,117,2), AU("Au","Gold",AggregationState.SOLID,ElementType.TRANSITION_METAL,79,11,118,3),
        HG("Hg","Mercury",AggregationState.LIQUID,ElementType.TRANSITION_METAL,80,12,121,2), TL("Tl","Thallium",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,81,13,123,3),
        PB("Pb","Lead",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,82,14,125,2), BI("Bi","Bismuth",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,83,15,126,3),
        PO("Po","Polonium",AggregationState.SOLID,ElementType.POST_TRANSITION_METAL,84,16,125,4), AT("At","Astatine",AggregationState.SOLID,ElementType.METALLOID,85,17,125,-1),
        RN("Rn","Radon",AggregationState.GAS,ElementType.NOBLE_GAS,86,18,136,0),
    FR("Fr","Francium",AggregationState.SOLID,ElementType.ALKALI_METAL,87,1,136,1), RA("Ra","Radium",AggregationState.SOLID,ElementType.ALKALINE_EARTH_METAL,88,2,138,2),
    AC("Ac","Actinium",AggregationState.SOLID,ElementType.ACTINIDE,89,0,138,3), TH("Th","Thorium",AggregationState.SOLID,ElementType.ACTINIDE,90,0,142,4),
        PA("Pa","Protactinium",AggregationState.SOLID,ElementType.ACTINIDE,91,0,140,5), U("U","Uranium",AggregationState.SOLID,ElementType.ACTINIDE,92,0,146,6),
        NP("Np","Naptunium",AggregationState.SOLID,ElementType.ACTINIDE,93,0,144,7), PU("Pu","Plutonium",AggregationState.SOLID,ElementType.ACTINIDE,94,0,150,7),
        AM("Am","Americium",AggregationState.SOLID,ElementType.ACTINIDE,95,0,148,3), CM("Cm","Curium",AggregationState.SOLID,ElementType.ACTINIDE,96,0,151,3),
        BK("Bk","Berkelium",AggregationState.SOLID,ElementType.ACTINIDE,97,0,150,3), CF("Cf","Californium",AggregationState.SOLID,ElementType.ACTINIDE,98,0,153,3),
        ES("Es","Einsteinium",AggregationState.SOLID,ElementType.ACTINIDE,99,0,155,3), FM("Fm","Fermium",AggregationState.UNKNOWN,ElementType.ACTINIDE,100,0,157,3),
        MD("Md","Mendelevium",AggregationState.UNKNOWN,ElementType.ACTINIDE,101,0,157,3), NO("No","Nobelium",AggregationState.UNKNOWN,ElementType.ACTINIDE,102,0,157,2),
        LR("Lr","Lawrencium",AggregationState.UNKNOWN,ElementType.ACTINIDE,103,0,159,3),
    RF("Rf","Rutherfordium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,104,4,157,4), DB("Db","Dubnium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,105,5,157,0),
        SG("Sg","Seaborgium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,106,6,160,0), BH("Bh","Bohrium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,107,7,157,0),
        HS("Hs","Hassium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,108,8,161,0), MT("Mt","Meitnerium",AggregationState.UNKNOWN,ElementType.UNKNOWN,109,9,159,0),
        DS("Ds","Darmstadtium",AggregationState.UNKNOWN,ElementType.UNKNOWN,110,10,159,0), RG("Rg","Roentgenium",AggregationState.UNKNOWN,ElementType.UNKNOWN,111,11,161,0),
        CN("Cn","Copernicium",AggregationState.UNKNOWN,ElementType.TRANSITION_METAL,112,12,165,0);
        /*UUT("Uut","Ununtrium",AggregationState.UNKNOWN,ElementType.UNKNOWN,113,13,0,0),
        FL("Fl","Flerovium",AggregationState.UNKNOWN,ElementType.POST_TRANSITION_METAL,114,14,0,0), UUP("Uup","Ununpentium",AggregationState.UNKNOWN,ElementType.UNKNOWN,115,15,0,0),
        LV("Lv","Livermorium",AggregationState.UNKNOWN,ElementType.UNKNOWN,116,16,0,0), UUS("Uus","Ununseptium",AggregationState.UNKNOWN,ElementType.UNKNOWN,117,17,0,0),
        UUO("Uuo","Ununoctium",AggregationState.UNKNOWN,ElementType.UNKNOWN,118,18,0,0);*/

    /** 1-3 letter symbol used for the element in the periodic table */
    public final String symbol;
    /** Aggregation state in which the element is found in normal conditions */
    public final AggregationState state;
    /** Type of the element */
    public final ElementType type;
    /** Atomic number */
    public final int atomicNumber;
    /** Group number in which the element can be found in the periodic table */
    public final int group;
    /** English name of the element */
    public final String fullName;
    /** Most common charge */
    public final int commonCharge;
    /** Neutrons count in the most common isotope */
    public final int commonNeutrons;

    private Element(String symbol, String fullName, AggregationState state, ElementType type,
            int atomicNumber, int group, int commonNeutrons, int commonCharge) {
        this.symbol = symbol;
        this.state = state;
        this.type = type;
        this.atomicNumber = atomicNumber;
        this.commonNeutrons = commonNeutrons;
        this.commonCharge = commonCharge;
        this.group = group;
        this.fullName = fullName;
    }

    /**
     * Determines the css style used for presentation of this element. The style is determined based on the
     * {@link ElementType} (background) and {@link AggregationState} (text).
     *
     * @return the css style
     */
    public String getStyle() {
        String style = "";
        switch (type) {
            case ACTINIDE :
                style = "-fx-background-color: #FF99CC, linear-gradient(#FF77AA, #FFA9DC), radial-gradient(center 50% -40%, radius 200%, #FF99CC 45%, #FFA9DC 50%);";
                break;
            case LANTHANIDE :
                style = "-fx-background-color: #FFBFff, linear-gradient(#FF99FF, #FFCFFF), radial-gradient(center 50% -40%, radius 200%, #FFBFff 45%, #FFCFFF 50%);";
                break;
            case ALKALI_METAL :
                style = "-fx-background-color: #FF6666, linear-gradient(#FF1111, #FF7676), radial-gradient(center 50% -40%, radius 200%, #FF6666 45%, #FF7676 50%);";
                break;
            case ALKALINE_EARTH_METAL :
                style = "-fx-background-color: #FFDEAD, linear-gradient(#FFBD5B, #FFEEBD), radial-gradient(center 50% -40%, radius 200%, #FFDEAD 45%, #FFEEBD 50%);";
                break;
            case DIATOMIC_NONMETAL :
                style = "-fx-background-color: #E7FF8F, linear-gradient(#B1FF00, #F7FF9F), radial-gradient(center 50% -40%, radius 200%, #E7FF8F 45%, #F7FF9F 50%);";
                break;
            case METALLOID :
                style = "-fx-background-color: #CCCC99, linear-gradient(#A0A050, #DCDC99), radial-gradient(center 50% -40%, radius 200%, #CCCC99 45%, #DCDC99 50%);";
                break;
            case NOBLE_GAS :
                style = "-fx-background-color: #DCDCFF, linear-gradient(#AAAAFF, #ECECFF), radial-gradient(center 50% -40%, radius 200%, #DCDCFF 45%, #ECECFF 50%);";
                break;
            case POLYATOMIC_NONMETAL :
                style = "-fx-background-color: #A1FFC3, linear-gradient(#00FF59, #B1FFD3), radial-gradient(center 50% -40%, radius 200%, #A1FFC3 45%, #B1FFD3 50%);";
                break;
            case POST_TRANSITION_METAL :
                style = "-fx-background-color: #CCCCCC, linear-gradient(#AAAAAA, #CCDCDC), radial-gradient(center 50% -40%, radius 200%, #CCCCCC 45%, #CCDCDC 50%);";
                break;
            case TRANSITION_METAL :
                style = "-fx-background-color: #FFC0C0, linear-gradient(#FFAAAA, #FFD0D0), radial-gradient(center 50% -40%, radius 200%, #FFC0C0 45%, #FFD0D0 50%);";
                break;
            case UNKNOWN :
            default :
                break;
        }
        switch (state) {
            case GAS : style += "-fx-text-fill: red;";
                break;
            case LIQUID : style += "-fx-text-fill: green;";
                break;
            case SOLID : style += "-fx-text-fill: black;";
                break;
            case UNKNOWN :
            default : style += "-fx-text-fill: grey;";
        }

        return style;
    }
}
