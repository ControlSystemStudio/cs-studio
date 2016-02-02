package org.csstudio.saverestore.ui.browser.periodictable;

import java.util.Locale;
import java.util.Objects;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Branch;

/**
 *
 * <code>Isotope</code> describes a single isotope of an element with a specific charge.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Isotope extends BaseLevel {

    private static final long serialVersionUID = -3119527921225304647L;

    /** The periodic table element */
    public final Element element;
    /** Number of neutrons of this isotope */
    public final int neutrons;
    /** Ion charge of this isotope */
    public final int charge;

    /**
     * Construct a new Isotope from pieces.
     *
     * @param element the element
     * @param neutrons the number of neutrons
     * @param charge the ion charge
     * @return the isotope
     */
    public static final Isotope of(Element element, int neutrons, int charge) {
        return new Isotope(element, neutrons, charge);
    }

    /**
     * Construct a new isotope from the storage name and returns the isotope if it could be created or null if it failed
     * to create it.
     *
     * @see Isotope#of(String)
     * @param storageName the storage name in format <code>symbol_mass_chargep/n_energy</code>
     * @return the isotope if the storage name matches the rules or null if the isotope could not be created
     */
    static final Isotope ofFlat(String storageName) {
        try {
            return Isotope.of(storageName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Construct a new isotope from the storage name which is expected to be in format
     * <code>symbol_mass_chargep/n_energy<code>, where <code>symbol</code> is a 1-3 letter chemical symbol,
     * <code>mass</code> is the atomic mass (number of neutrons plus number of protons), <code>charge</code> is the
     * absolute ion charge, followed by <code>p</code> for positive charge or <code>n</code> for negative charge. The
     * last part of the name (<code>energy</code>) defines the energy of the ion.
     *
     * @param storageName the storage name in format <code>symbol_mass_chargep/n_energy</code>
     * @return the isotope
     * @throws IllegalArgumentException if the storage name does not match the rules
     */
    public static final Isotope of(String storageName) throws IllegalArgumentException {
        String[] parts = storageName.split("\\_");
        Element e = null;
        try {
            e = Element.valueOf(parts[0].toUpperCase(Locale.UK));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("'" + parts[0] + "' is not a valid name.", ex);
        }
        if (parts.length < 3) {
            throw new IllegalArgumentException(storageName + " does not provide mass, charge and energy.");
        } else if (parts.length > 3) {
            throw new IllegalArgumentException("Too many parts in the name.");
        }

        int neutrons = Integer.parseInt(parts[1]) - e.atomicNumber;
        if (neutrons < 0) {
            throw new IllegalArgumentException(
                "Mass is too small for '" + e.fullName + "' (min " + e.atomicNumber + ").");
        }

        char pn = parts[2].charAt(parts[2].length() - 1);
        if (!(pn == 'n' || pn == 'p')) {
            throw new IllegalArgumentException("The charge sign is not defined (p or n) for " + storageName + ".");
        }
        int charge = Integer.parseInt(parts[2].substring(0, parts[2].length() - 1));
        charge *= pn == 'n' ? -1 : 1;
        if (charge > e.atomicNumber) {
            throw new IllegalArgumentException(
                "Charge of '" + e.fullName + "' cannot be higher than " + e.atomicNumber + ".");
        }

        return new Isotope(e, neutrons, charge);
    }

    private Isotope(Element element, int neutrons, int charge) {
        super(null, composeStorageName(element, neutrons, charge), composePresentationName(element, neutrons, charge));
        this.element = element;
        this.neutrons = neutrons;
        this.charge = charge;
    }

    /**
     * Compose the storage name for the given isotope parameters.
     *
     * @param element the element
     * @param neutrons the number of neutrons in the isotope
     * @param charge the isotope charge
     * @return the storage representation for the isotope
     */
    private static String composeStorageName(Element element, int neutrons, int charge) {
        return new StringBuilder().append(element.symbol).append('_').append(element.atomicNumber + neutrons)
            .append('_').append(Math.abs(charge)).append(charge < 0 ? 'n' : 'p').toString();
    }

    /**
     * Compose the presentation name for the given isotope parameters;
     *
     * @param element the element
     * @param neutrons the number of neutrons in the isotope
     * @param charge the isotope charge
     * @return the presentation name for the isotope
     */
    private static String composePresentationName(Element element, int neutrons, int charge) {
        StringBuilder s = new StringBuilder(11);
        for (char c : String.valueOf(element.atomicNumber).toCharArray()) {
            s.append(getUnicode(c, true));
        }
        s.append(element.symbol);
        for (char c : String.valueOf(neutrons + element.atomicNumber).toCharArray()) {
            s.append(getUnicode(c, false));
        }
        for (char c : String.valueOf(Math.abs(charge)).toCharArray()) {
            s.append(getUnicode(c, true));
        }
        if (charge != 0) {
            s.append(charge < 0 ? '\u207b' : '\u207a');
        }
        return s.toString();
    }

    /**
     * Returns the unicode character in superscript or subscript style for the given character. Only numeric characters
     * are supported.
     *
     * @param c the one digit number as a character which needs to be styled
     * @param superscript true if a superscript code or false if a subscript code is requested
     * @return the character
     */
    private static char getUnicode(char c, boolean superscript) {
        if (superscript) {
            switch (c) {
                case '0':
                    return '\u2070';
                case '1':
                    return '\u00B9';
                case '2':
                    return '\u00B2';
                case '3':
                    return '\u00B3';
                case '4':
                    return '\u2074';
                case '5':
                    return '\u2075';
                case '6':
                    return '\u2076';
                case '7':
                    return '\u2077';
                case '8':
                    return '\u2078';
                case '9':
                    return '\u2079';
            }
        } else {
            switch (c) {
                case '0':
                    return '\u2080';
                case '1':
                    return '\u2081';
                case '2':
                    return '\u2082';
                case '3':
                    return '\u2083';
                case '4':
                    return '\u2084';
                case '5':
                    return '\u2085';
                case '6':
                    return '\u2086';
                case '7':
                    return '\u2087';
                case '8':
                    return '\u2088';
                case '9':
                    return '\u2089';
            }
        }
        return c;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.data.BaseLevel#getBranch()
     */
    @Override
    public Branch getBranch() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(charge, element, neutrons);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Isotope other = (Isotope) obj;
        return Objects.equals(charge, other.charge) && Objects.equals(element, other.element)
            && Objects.equals(neutrons, other.neutrons);
    }
}
