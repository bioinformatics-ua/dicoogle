/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.core.settings;

import org.slf4j.LoggerFactory;
import pt.ua.dicoogle.sdk.datastructs.MoveDestination;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Eduardo Pinho <eduardopinho@ua.pt>
 */
class SettingsAdapters {

    static class MoveDestinationPojo {
        @XmlAttribute(name = "ae")
        public String ae;
        @XmlAttribute(name = "ip")
        public String ip;
        @XmlAttribute(name = "port")
        public int port;
        @XmlAttribute(name = "public")
        public boolean ispublic;
        @XmlAttribute(name = "description")
        public String description;

        MoveDestinationPojo() {}

        MoveDestinationPojo(MoveDestination dest) {
            this.ae = dest.getAETitle();
            this.ip = dest.getIpAddrs();
            this.port = dest.getPort();
            this.ispublic = dest.isIsPublic();
            this.description = dest.getDescription();
        }
    }

    static class MoveDestinationAdapter extends XmlAdapter<MoveDestinationPojo, MoveDestination> {

        @Override
        public MoveDestination unmarshal(MoveDestinationPojo o) throws Exception {
            LoggerFactory.getLogger(MoveDestinationAdapter.class).info("unmarshal: {}", o);
            if (o == null) return null;
            return new MoveDestination(o.ae, o.ip, o.port, o.ispublic, o.description);
        }

        @Override
        public MoveDestinationPojo marshal(MoveDestination o) throws Exception {
            LoggerFactory.getLogger(MoveDestinationAdapter.class).info("marshal: {}", o);
            if (o == null) return null;
            return new MoveDestinationPojo(o);
        }
    }

}
