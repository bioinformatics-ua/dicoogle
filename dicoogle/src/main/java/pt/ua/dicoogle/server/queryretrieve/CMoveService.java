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
package pt.ua.dicoogle.server.queryretrieve;

import java.io.IOException;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.service.CMoveSCP;
import org.dcm4che2.net.service.DicomService;
import java.util.concurrent.Executor;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.SingleDimseRSP;
import org.dcm4che2.net.Status;
/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CMoveService extends DicomService implements CMoveSCP
{


    private final Executor executor;

    public CMoveService(String[] sopClasses, Executor executor)
    {
        super(sopClasses);
        this.executor = executor;
    }

    public CMoveService(String sopClass, Executor executor)
    {
        super(sopClass);
        this.executor = executor;
    }

@Override
    public void cmove(Association as, int pcid, DicomObject rq,
            DicomObject data) throws DicomServiceException, IOException
    {
                //DebugManager.getInstance().debug("just cmove");

        //DebugManager.getInstance().debug(CommandUtils.toString(rq, pcid, "1.2.2.2.2.2.2.0"));
        DicomObject cmdrsp = CommandUtils.mkRSP(rq, CommandUtils.SUCCESS);
        DimseRSP rsp = doCMove(as, pcid, rq, data, cmdrsp);
        try {
            rsp.next();
        } catch (InterruptedException e) {
            throw new DicomServiceException(rq, Status.ProcessingFailure);
        }
        cmdrsp = rsp.getCommand();
        if (CommandUtils.isPending(cmdrsp))
        {
            as.registerCancelRQHandler(rq, rsp);
            //executor.execute(new WriteMultiDimseRsp(as, pcid, rsp));
        }
        else
        {
            as.writeDimseRSP(pcid, cmdrsp, rsp.getDataset());
        }
    }
    protected DimseRSP doCMove(Association as, int pcid, DicomObject cmd,
            DicomObject data, DicomObject rsp) throws DicomServiceException
    {
        return new SingleDimseRSP(rsp);
    }

}
