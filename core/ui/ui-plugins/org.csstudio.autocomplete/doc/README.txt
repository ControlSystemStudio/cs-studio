UML schemas done using Eclipse Papyrus plugin, see:
http://www.eclipse.org/papyrus/

Some schemes describe how the parsers/providers are handled by the service and provide 2 examples to show the parser/provider class behavior:
- AutoCompleteService: sequence diagram which describes how the field content is handled by the service using parsers and providers.
- FormulaProvider: class diagram for Formula Function Provider which shows the common classes useful to providers.
- SimParser: class diagram for Sim Parser which shows the common classes useful to parsers.

1. PARSERS

In order to support the new requirements for formula auto-completion which implies to be able to auto-complete a PV in the middle of a formula, we had to introduce a new concept: IContentParser.

Before calling providers, the main service (AutoCompleteService) looks for ALL implemented parsers which handle the content by calling the accept(ContentDescriptor) method.
Parsers are defined the same way that providers via OSGI services.
A parser must return a ContentDescriptor with a ContentType and its corresponding value which is handled by providers (instead of directly handling the entire content).
However, the original content remains accessible in the descriptor.
The generated descriptor can be submitted again to parsers via the "replay" Boolean.

Example: 
The method accept of the formula parser returns "true" if the content start with "=" and the AutoCompleteType of the field is "Formula".
If the content is "=1+sin('sim://s" the formula parser returns a descriptor with ContentType="PV", value="sim://s" and replay="true".
This descriptor is submitted to parsers again and handled by the SimContentParser to provide a descriptor with ContentType="SimFunction" and value="s".
This descriptor is handled by the SimContentProvider which only accepts ContentType=="SimFunction" and provides all available functions for simulated PV starting by "s".

The descriptor contains a start index which can defined by parsers (default=0) and has to be used to set Proposal insertion position. 
This way, the selected proposal is inserted after the quote.

Example: 
If the original content of the field is "=1+sin('sim://sy", selecting the proposals "sim://system." with insertionPos=8 results to "=1+sin('sim://system.".
The service set the default data source from preferences in the first submitted descriptor and reset it each time the provided descriptor has to be submitted to parsers. 
This is useful for PV parser. 

Some attributes of the ContentDescriptor are always overwritten by the AutoCompleteService:
- originalContent
- autoCompleteType
- defaultDataSource
- start/end indexes are added up when the descriptor is replayed.

Parsers have no priority management. The main service loops on ALL defined and get a list of ContentDescriptor.
If no parser handles the content, a ContentDescriptor with ContentType="Undefined" and value==originalContent is created and added to the list.

Some common parsers are implemented within the auto-complete plugin.
PVParser handles the parsing of PV name/field/parameters and return a PVDescriptor with types PVName/PVField/PVParam.
These types can be accepted by a site-specific PV provider.

Formula, Sim, Sys and Loc parsers/providers have been implemented in separated plugins (o.c.autocomplete.pvmanager.*).

A site-specific parser can use the common descriptors/types or define its owns by extending ContentDescriptor and/or ContentType.
In this way, the site-specific provider just have to accept only the defined types and cast the descriptor.

2. PROVIDERS

Once the service get the ContentDescriptor list, it associates a ContentDescriptor to an IAutoCompleteProvider by calling the accept(ContentType) method.
The service retrieves a list of defined providers and for each ContentDescriptor, it loops on the provider list.
If a provider accept the descriptor, it is removed from it list until there is no provider available.
In this way, if the first descriptor of the list is accepted by all providers, all other descriptor will be ignored...

The list of provider is built from preferences ordered list.
Then, ALL "high level" providers are put at the beginning of the list.
These providers are mutually exclusives and defined via an attribute of the OSGI component (default=false).
High level providers does not need to be added to preferences.

Example: 
If the preference is "org.csstudio.autocomplete/providers.Formula=History,4; DB Files; Archive RDB" and if formula function, sin, sys and loc plugins are defined.
The defined list will be:
[[Formula Function, Sim DS, Sys DS, Loc DS](no order), History, DB Files, Archive RDB]

In the implementation of IAutoCompleteProvider, the received ContentDescriptor can be casted to a more specific one.

Example: 
A provider seeking for ContentType=="PVName" can cast the ContentDescriptor to PVDescriptor instancied by the PVParser to access more specific attributes.

Regarding Proposal definition, an important element to take in account is the start index of the ContentDescriptor.
It have to be used to define an insertion position.
The originalContent attribute of Proposal is overwritten when a Proposal is added to ContentProposalList managed by the UI.
The ContentProposalList is reset on each search and the originalContent is set the by the UI.

The provider is free to decide how it handles wildcards, tooltip and styling (of both proposals and tooltips).
Common styles are defined in ProposalStyle (getDefault, getError, ...).
AutoCompleteHelper also provides some methods to help wildcards translation to SQL request or regular expressions.

