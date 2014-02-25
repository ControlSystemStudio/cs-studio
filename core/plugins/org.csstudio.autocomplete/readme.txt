Each auto-completed field comes with a type. 
The type allows users to define a specific behavior for auto-completed fields.
Each type handles an ordered provider list and a number of maximum displayed results per provider.
If no provider is defined for a type, the history with default number of maximum displayed results will be taken.
If no maximum displayed results is defined for a provider, the default one will be used.

Preferences must be defined using the following pattern:

providers.TYPE=provider_name[,max_results];provider_name[,max_results]...
providers.ANOTHER_TYPE=provider_name[,max_results];provider_name[,max_results]...

Providers results will be displayed in the same order as specified in the preference.
