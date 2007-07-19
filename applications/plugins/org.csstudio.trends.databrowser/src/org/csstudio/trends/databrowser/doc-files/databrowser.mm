<map version="0.8.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#ff0000" CREATED="1155840113546" ID="Freemind_Link_1256490097" MODIFIED="1155936938300" TEXT="Data Browser&#xa;Application">
<font BOLD="true" NAME="SansSerif" SIZE="14"/>
<node COLOR="#000000" CREATED="1155840172422" HGAP="38" ID="_" MODIFIED="1158682428861" POSITION="right" STYLE="bubble" TEXT="Model" VSHIFT="-9">
<font NAME="SansSerif" SIZE="12"/>
<icon BUILTIN="desktop_new"/>
<node CREATED="1155840175950" ID="Freemind_Link_1315106477" MODIFIED="1184683193959" TEXT="AbstractModelItem" VSHIFT="4">
<font NAME="SansSerif" SIZE="12"/>
<node CREATED="1155840196383" HGAP="45" ID="Freemind_Link_102027334" MODIFIED="1164232142222" TEXT="(PV-) Name" VSHIFT="141"/>
<node CREATED="1158179285550" HGAP="42" ID="Freemind_Link_1602308751" MODIFIED="1164232139812" TEXT="Y Axis Index, Range (min, max), Color, Line width" VSHIFT="6"/>
<node CREATED="1184683197115" HGAP="43" ID="Freemind_Link_961637321" MODIFIED="1184683355885" TEXT="PVModelItem" VSHIFT="-2">
<node CREATED="1155840179778" HGAP="38" ID="Freemind_Link_866944297" MODIFIED="1184683335591" TEXT="PV" VSHIFT="181">
<node CREATED="1155842911492" ID="Freemind_Link_719856090" MODIFIED="1162417148423" STYLE="fork" TEXT="receives value updates..." VSHIFT="-8"/>
</node>
<node CREATED="1155842580742" HGAP="38" ID="Freemind_Link_1828051111" MODIFIED="1164232136015" TEXT="Current Value, severity, status" VSHIFT="-9"/>
<node CREATED="1158681842559" HGAP="39" ID="Freemind_Link_1293033052" MODIFIED="1164232137386" TEXT="IArchiveDataSource[]" VSHIFT="-2">
<node CREATED="1158180474124" HGAP="21" ID="Freemind_Link_339129947" MODIFIED="1158681872840" TEXT="URL" VSHIFT="-1"/>
<node CREATED="1158180476632" ID="Freemind_Link_802398111" MODIFIED="1158681861654" TEXT="Key"/>
</node>
<node CREATED="1164231268360" HGAP="39" ID="Freemind_Link_1765617771" MODIFIED="1184683396903" TEXT="ModelSamples" VSHIFT="-142">
<node CREATED="1158180461094" HGAP="24" ID="Freemind_Link_964945863" MODIFIED="1164232157688" TEXT="ModelSampleArray" VSHIFT="15">
<font NAME="SansSerif" SIZE="12"/>
<node CREATED="1164231376469" HGAP="22" ID="Freemind_Link_1966471643" MODIFIED="1164231402271" STYLE="fork" TEXT="Historic Samples" VSHIFT="-8"/>
</node>
<node CREATED="1158681920583" HGAP="25" ID="Freemind_Link_465798410" MODIFIED="1164232154529" TEXT="ModelSampleRing" VSHIFT="-8">
<node CREATED="1155840182477" HGAP="29" ID="Freemind_Link_612720850" MODIFIED="1164231552708" STYLE="fork" TEXT="Ringbuffer for live samples" VSHIFT="-6">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node CREATED="1164990266807" ID="Freemind_Link_965035988" MODIFIED="1165013996542" STYLE="fork" TEXT="ModelSample looks like...">
<node CREATED="1164990241260" ID="Freemind_Link_1009011039" MODIFIED="1164990282562" STYLE="fork" TEXT="ChartSampleSequence for plotting," VSHIFT="2"/>
<node CREATED="1164990283720" ID="Freemind_Link_1471130849" MODIFIED="1164990303195" STYLE="fork" TEXT="Archive Sample Iterator for export"/>
</node>
</node>
</node>
<node CREATED="1184683203936" HGAP="45" ID="Freemind_Link_762326619" MODIFIED="1184683354456" TEXT="FormulaModelItem" VSHIFT="-138">
<node CREATED="1184683238286" ID="Freemind_Link_514126569" MODIFIED="1184683247375" TEXT="Formula"/>
<node CREATED="1184683255722" ID="Freemind_Link_1646427684" MODIFIED="1184683257474" TEXT="Inputs"/>
<node CREATED="1184683248723" ID="Freemind_Link_1620856790" MODIFIED="1184683255182" TEXT="ModelSampleArray"/>
</node>
</node>
</node>
<node CREATED="1155938814319" HGAP="60" ID="Freemind_Link_1333792926" MODIFIED="1184683497637" POSITION="right" TEXT="Controller" VSHIFT="18">
<icon BUILTIN="penguin"/>
<node CREATED="1155843846384" HGAP="41" ID="Freemind_Link_1636229449" MODIFIED="1163003569639" TEXT="ScannerAndScroller" VSHIFT="6">
<node CREATED="1155842752003" HGAP="26" ID="Freemind_Link_1985827728" MODIFIED="1158682349812" STYLE="fork" TEXT="Periodically (configurable rate) add&#xa;Current Value&#xa;to Live Samples." VSHIFT="6">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1158682271000" ID="Freemind_Link_1948437931" MODIFIED="1158682329327" STYLE="fork" TEXT="if enabled.." VSHIFT="-18">
<node CREATED="1156187782593" HGAP="23" ID="Freemind_Link_667836098" MODIFIED="1158682354137" STYLE="fork" TEXT="Scroll and/or redraw&#xa;at some rate" VSHIFT="-5"/>
</node>
</node>
<node CREATED="1162484239672" HGAP="38" ID="Freemind_Link_832605916" MODIFIED="1163001060029" TEXT="Handle &apos;drop&apos; into Plot Editor Chart" VSHIFT="3"/>
<node CREATED="1162841410334" HGAP="37" ID="Freemind_Link_1549053786" MODIFIED="1163001062700" TEXT="Chart &lt;-&gt; Model sync" VSHIFT="5">
<node CREATED="1162841379073" ID="Freemind_Link_562153928" MODIFIED="1162845653396" STYLE="fork" TEXT="&lt;html&gt;&#xa;When model config changes, update the chart&lt;br&gt;&#xa;When chart zoom/pan changes, update model&lt;br&gt;&#xa;But: avoid infinite loops."/>
</node>
<node CREATED="1162585400275" HGAP="36" ID="Freemind_Link_386001227" MODIFIED="1163001067236" TEXT="Get archive data for new items or changed X Axis" VSHIFT="1">
<icon BUILTIN="help"/>
</node>
<node CREATED="1158178709017" HGAP="38" ID="Freemind_Link_1955087667" MODIFIED="1163003564569" TEXT="Archive Fetch Job" VSHIFT="-2">
<font NAME="SansSerif" SIZE="12"/>
<node CREATED="1158680191186" ID="Freemind_Link_802101594" MODIFIED="1163007179789" STYLE="fork" TEXT="Get New Samples via ArchiveCache..." VSHIFT="-7"/>
</node>
</node>
<node CREATED="1158329015494" HGAP="24" ID="Freemind_Link_16162630" MODIFIED="1162494612426" POSITION="right" TEXT="GUI" VSHIFT="27">
<icon BUILTIN="xmag"/>
<node CREATED="1155840202741" HGAP="60" ID="Freemind_Link_509686660" MODIFIED="1162494595481" STYLE="bubble" TEXT="Plot &apos;Editor&apos;" VSHIFT="11">
<font NAME="SansSerif" SIZE="12"/>
<node CREATED="1158329095370" ID="Freemind_Link_1737950886" MODIFIED="1158329117563" TEXT="Main GUI, has reference to input (= model)" VSHIFT="-9"/>
<node CREATED="1155936847379" HGAP="21" ID="Freemind_Link_486523671" MODIFIED="1162484265017" TEXT="(Interactive) Chart" VSHIFT="-6">
<node CREATED="1158179071189" HGAP="24" ID="Freemind_Link_1678575166" MODIFIED="1162841368214" STYLE="fork" TEXT="inform about user pan/zoom" VSHIFT="-6"/>
<node CREATED="1162480069006" ID="Freemind_Link_735072009" MODIFIED="1162484227805" STYLE="fork" TEXT="get yaxis for given x/y">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node CREATED="1155936858314" ID="Freemind_Link_1817288410" MODIFIED="1155937176997" TEXT="Additional &quot;Scroll&quot; Button" VSHIFT="5"/>
<node CREATED="1162498892610" ID="Freemind_Link_151236732" MODIFIED="1162499000392" STYLE="fork" TEXT="&lt;html&gt;&#xa;Linked to one model.&lt;br&gt;&#xa;The controller interfaces with the model."/>
</node>
<node CREATED="1158329060321" ID="Freemind_Link_1490315186" MODIFIED="1162480113716" TEXT="Archive View">
<font NAME="SansSerif" SIZE="12"/>
<node CREATED="1158592831455" ID="Freemind_Link_1029247718" MODIFIED="1158592833896" TEXT="URL">
<node CREATED="1158592835354" HGAP="73" ID="Freemind_Link_47068335" MODIFIED="1162415591976" TEXT="ArchiveServer instance"/>
</node>
<node CREATED="1162415499541" ID="Freemind_Link_727773507" MODIFIED="1162415539301" TEXT="archive table">
<node CREATED="1158592841253" ID="Freemind_Link_289572138" MODIFIED="1162415565624" TEXT="List of archives/keys/description" VSHIFT="2"/>
</node>
<node CREATED="1162415453735" ID="Freemind_Link_1795022342" MODIFIED="1162415524286" TEXT="Pattern"/>
<node CREATED="1162415524914" ID="Freemind_Link_644188980" MODIFIED="1162415530193" TEXT="name table">
<node CREATED="1162415541049" HGAP="30" ID="Freemind_Link_87079740" MODIFIED="1162415599724" TEXT="PV, Archive, start/end" VSHIFT="1"/>
</node>
<node CREATED="1162498801919" ID="Freemind_Link_1322569521" MODIFIED="1162499051162" STYLE="fork" TEXT="Shared; modifies the  model of the active Plot Editor"/>
</node>
<node CREATED="1158329029301" HGAP="25" ID="Freemind_Link_801846425" MODIFIED="1162498797361" TEXT="Config View" VSHIFT="13">
<node CREATED="1158682450781" ID="Freemind_Link_308436743" MODIFIED="1158682454023" TEXT="List of channels">
<node CREATED="1158682470177" ID="Freemind_Link_1801026006" MODIFIED="1158682478385" TEXT="name, color"/>
</node>
<node CREATED="1158682455231" ID="Freemind_Link_344678940" MODIFIED="1158682511504" TEXT="List of archives for selected channel"/>
<node CREATED="1162498801919" ID="Freemind_Link_60561984" MODIFIED="1162499051162" STYLE="fork" TEXT="Shared; modifies the  model of the active Plot Editor"/>
</node>
</node>
<node CREATED="1163007107850" ID="Freemind_Link_1667142883" MODIFIED="1163013894418" POSITION="left" TEXT="ArchiveCache" VSHIFT="34">
<icon BUILTIN="bookmark"/>
<node CREATED="1163013907632" ID="Freemind_Link_1078503596" MODIFIED="1163014734454" STYLE="fork" TEXT="Singleton"/>
<node CREATED="1163013940431" ID="Freemind_Link_1685652928" MODIFIED="1163013953948" STYLE="fork" TEXT="Caches ArchiveServers and values"/>
</node>
</node>
</map>
