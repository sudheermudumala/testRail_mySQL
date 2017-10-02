# Change Log
All changes to this project will be documented in this file.
Format: - <date>(<PR #>) - <ticket #> - <summary> @<initials>

## [No release]
- 05-11-2017(none) - none - Fixed script issue for moonrise from WxAssertHelper#assertAstroTime --> WxAssertUtils#timesWithinRange. @AS
- 05-09-2017(#25) - WEBQAK_1731 - Add_month,_short_day_short_month_from_translations_object(MERGED). @AS
- 04-21-2017(#25) - WEBQAK-1731 - Added translation support for Full/ half day/month names. @AS
- 05-01-2017(none) - none - Added breathing and pollen APIs for WX_CALL @RL
- 04-20-2017(none) - none - Refactored WX_ASSERT#assertPrecipPercentage logic to fix decimal rounding issue and renamed method to #assertPrecip. @AS
- 04-18-2017(none) - none - Refactored WX_ASSERT#assertPrecipPercentage logic to fix failures. @AS
- 04-18-2017(#24) - none - Moved localization logic from core to TWC. @AS
- 04-11-2017(#23) - WEBQAK-1722 - Added check for coordinate in WX_JSON_INDEX @RL
- 04-11-2017(#22) - WEBQAK-1716 - Deleted LocaleUtility class and it's unit tests from core project and moved it to TWC project. @AS
- 04-06-2017(none) - none - Fixed translation issue from WxAssertHelper#assertAstroMoonset, Added transtaions for date formats. @AS
- 04-06-2017(none) - none - Fixed translation issue from WxDate.java, Added translations to get locale specific date formats. @AS
- 04-10-2017(none) - none - Fixed location ID regex @RL
- 04-06-2017(none) - none - Added +-1 UV assert. Changed random loc id DP limit to 5 @RL
- 04-05-2017(none) - none - Added method param to data provider listener @RL 
- 04-04-2017(none) - none - Updated temperature assert to handle decimals @RL
- 04-04-2017(none) - none - Added formatting to expected temperature assert @AS
- 03-31-2017(#20) - WEBQAK-1663 - Added data provider listener for parsing location id CSV @RL
- 03-29-2017(#19) - WEBQAK-1699 - Added date range assert for astro times @RL
- 03-24-2017(#18) - WEBQAK-1687 - Added URL translations for all locales @RL
