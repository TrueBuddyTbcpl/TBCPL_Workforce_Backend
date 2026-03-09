// com/tbcpl/workforce/common/constants/EmailDomainConstants.java
package com.tbcpl.workforce.common.constants;

import java.util.Set;

/**
 * Allowed email domains for employee accounts
 */
public final class EmailDomainConstants {

    private EmailDomainConstants() {}

    public static final String TBCPL_DOMAIN  = "tbcpl.co.in";
    public static final String GNSP_DOMAIN   = "gnsp.co.in";

    public static final Set<String> ALLOWED_DOMAINS = Set.of(TBCPL_DOMAIN, GNSP_DOMAIN);

    public static final String ALLOWED_DOMAINS_DISPLAY = "@tbcpl.co.in, @gnsp.co.in";
}
