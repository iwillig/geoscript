<?xml version="1.0" encoding="UTF-8"?><sld:UserStyle xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
  <sld:Name>OSM Planet line Style</sld:Name>
  <sld:Title/>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>primary</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:MinScaleDenominator>432562.34619140625</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>2.768399015625E7</sld:MaxScaleDenominator>
      <sld:TextSymbolizer>
        <sld:Label>
          <ogc:PropertyName/>
        </sld:Label>
        <sld:Font>
          <sld:CssParameter name="font-family">Arial</sld:CssParameter>
          <sld:CssParameter name="font-size">10</sld:CssParameter>
          <sld:CssParameter name="font-style">normal</sld:CssParameter>
          <sld:CssParameter name="font-weight">normal</sld:CssParameter>
        </sld:Font>
        <sld:Fill/>
        <sld:Graphic>
          <sld:ExternalGraphic>
            <sld:OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="file:image.gif"/>
            <sld:Format>image/gif</sld:Format>
          </sld:ExternalGraphic>
          <sld:Size>5</sld:Size>
        </sld:Graphic>
      </sld:TextSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>primary</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:MinScaleDenominator>432562.34619140625</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>2.768399015625E7</sld:MaxScaleDenominator>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:GraphicStroke>
            <sld:Graphic>
              <sld:Mark>
                <sld:WellKnownName>shape://vertline</sld:WellKnownName>
                <sld:Fill/>
                <sld:Stroke/>
              </sld:Mark>
              <sld:Size>12</sld:Size>
            </sld:Graphic>
          </sld:GraphicStroke>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <sld:MaxScaleDenominator>54070.29327392578</sld:MaxScaleDenominator>
      <sld:TextSymbolizer>
        <sld:Label>
          <ogc:PropertyName>name</ogc:PropertyName>
        </sld:Label>
        <sld:Font>
          <sld:CssParameter name="font-family">Droid Sans</sld:CssParameter>
          <sld:CssParameter name="font-size">10</sld:CssParameter>
          <sld:CssParameter name="font-style">normal</sld:CssParameter>
          <sld:CssParameter name="font-weight">normal</sld:CssParameter>
        </sld:Font>
        <sld:LabelPlacement>
          <sld:PointPlacement>
            <sld:AnchorPoint>
              <sld:AnchorPointX>0.7</sld:AnchorPointX>
              <sld:AnchorPointY>0.2</sld:AnchorPointY>
            </sld:AnchorPoint>
            <sld:Displacement>
              <sld:DisplacementX>10</sld:DisplacementX>
              <sld:DisplacementY>10</sld:DisplacementY>
            </sld:Displacement>
          </sld:PointPlacement>
        </sld:LabelPlacement>
        <sld:Halo>
          <sld:Radius>1</sld:Radius>
          <sld:Fill>
            <sld:CssParameter name="fill">#ffffff</sld:CssParameter>
            <sld:CssParameter name="fill-opacity"/>
          </sld:Fill>
        </sld:Halo>
        <sld:Fill>
          <sld:CssParameter name="fill">#1161d9</sld:CssParameter>
        </sld:Fill>
        <sld:VendorOption name="autoWrap">90</sld:VendorOption>
      </sld:TextSymbolizer>
      <sld:PointSymbolizer>
        <sld:Graphic>
          <sld:ExternalGraphic>
            <sld:OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="file:/home/ivan/dev/clojure/ogstudio/nielsen-data/icons/20_airtransportation.gif"/>
            <sld:Format>image/gif</sld:Format>
          </sld:ExternalGraphic>
          <sld:Mark>
            <sld:Fill>
              <sld:CssParameter name="fill">#1161d9</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#1161d9</sld:CssParameter>
            </sld:Stroke>
          </sld:Mark>
          <sld:Size>5</sld:Size>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:And>
          <ogc:PropertyIsEqualTo>
            <ogc:PropertyName>boundary</ogc:PropertyName>
            <ogc:Literal>administrative</ogc:Literal>
          </ogc:PropertyIsEqualTo>
          <ogc:PropertyIsEqualTo>
            <ogc:PropertyName>admin_level</ogc:PropertyName>
            <ogc:Literal>4</ogc:Literal>
          </ogc:PropertyIsEqualTo>
        </ogc:And>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke">#808080</sld:CssParameter>
          <sld:CssParameter name="stroke-dasharray">2.0 2.0</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>residential</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:MinScaleDenominator>432562.34619140625</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>2.2147192125E8</sld:MaxScaleDenominator>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke">#eeeeee</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>motorway</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke">#ffffff</sld:CssParameter>
          <sld:CssParameter name="stroke-width">6</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>secondary</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke">#eeeeee</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>primary</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:MinScaleDenominator>432562.34619140625</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>2.768399015625E7</sld:MaxScaleDenominator>
      <sld:LineSymbolizer>
        <sld:Stroke/>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>motorway</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke-linecap">round</sld:CssParameter>
          <sld:CssParameter name="stroke-linejoin">round</sld:CssParameter>
          <sld:CssParameter name="stroke-width">2</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>motorway</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke-linecap">round</sld:CssParameter>
          <sld:CssParameter name="stroke-linejoin">round</sld:CssParameter>
          <sld:CssParameter name="stroke-width">3</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>highway</ogc:PropertyName>
          <ogc:Literal>trunk</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke/>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <ogc:Filter>
        <ogc:Or>
          <ogc:PropertyIsEqualTo>
            <ogc:PropertyName>highway</ogc:PropertyName>
            <ogc:Literal>secondary</ogc:Literal>
          </ogc:PropertyIsEqualTo>
          <ogc:PropertyIsEqualTo>
            <ogc:PropertyName>highway</ogc:PropertyName>
            <ogc:Literal>primary</ogc:Literal>
          </ogc:PropertyIsEqualTo>
        </ogc:Or>
      </ogc:Filter>
      <sld:LineSymbolizer>
        <sld:Stroke/>
      </sld:LineSymbolizer>
      <sld:TextSymbolizer>
        <sld:Label>
          <ogc:PropertyName>name</ogc:PropertyName>
        </sld:Label>
        <sld:Font>
          <sld:CssParameter name="font-family">Droid Sans</sld:CssParameter>
          <sld:CssParameter name="font-size">13</sld:CssParameter>
          <sld:CssParameter name="font-style">normal</sld:CssParameter>
          <sld:CssParameter name="font-weight">normal</sld:CssParameter>
        </sld:Font>
        <sld:LabelPlacement>
          <sld:LinePlacement>
            <sld:PerpendicularOffset>10</sld:PerpendicularOffset>
          </sld:LinePlacement>
        </sld:LabelPlacement>
        <sld:Halo>
          <sld:Radius>2</sld:Radius>
          <sld:Fill>
            <sld:CssParameter name="fill">#ffffff</sld:CssParameter>
            <sld:CssParameter name="fill-opacity"/>
          </sld:Fill>
        </sld:Halo>
        <sld:Fill>
          <sld:CssParameter name="fill">#000000</sld:CssParameter>
        </sld:Fill>
        <sld:VendorOption name="followLine">true</sld:VendorOption>
      </sld:TextSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
</sld:UserStyle>
