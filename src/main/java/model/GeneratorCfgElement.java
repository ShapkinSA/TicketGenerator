package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "cfg")
public class GeneratorCfgElement {
    @XmlElement
    private GeneratorCfg generatorCfg;
}
