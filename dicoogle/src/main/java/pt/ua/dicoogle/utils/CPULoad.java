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
package pt.ua.dicoogle.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class CPULoad
{


    public CPULoad()
    {

    }

    public double cpuLoad()
    {

        ThreadMXBean TMB = ManagementFactory.getThreadMXBean();
        long time = new Date().getTime() * 1000000;
        long cput = 0;
        double cpuperc = -1;

        //Begin loop.

        if( TMB.isThreadCpuTimeSupported() )
        {
                if(new Date().getTime() * 1000000 - time > 1000000000) //Reset once per second
                {
                        time = new Date().getTime() * 1000000;
                        cput = TMB.getCurrentThreadCpuTime();
                }

                if(!TMB.isThreadCpuTimeEnabled())
                {
                        TMB.setThreadCpuTimeEnabled(true);
                }

                if(new Date().getTime() * 1000000 - time != 0)
                        cpuperc = (TMB.getCurrentThreadCpuTime() - cput) / (new Date().getTime() * 1000000.0 - time) * 100.0;
            }
        else
        {
            cpuperc = -2;
        }

        return cpuperc;

    }

    public static double getAvgCpu()
    {
        OperatingSystemMXBean osBean=ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemLoadAverage();
    }



    
    

    

}

