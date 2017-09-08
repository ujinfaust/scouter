/*
 *  Copyright 2015 the original author or authors.
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package scouterx.webapp.api.consumer;

import lombok.extern.slf4j.Slf4j;
import scouter.lang.constants.ParamConstant;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.XLogProfilePack;
import scouter.lang.step.Step;
import scouter.net.RequestCmd;
import scouterx.client.net.TcpProxy;
import scouterx.webapp.api.exception.ErrorState;
import scouterx.webapp.api.requestmodel.ProfileRequest;

import java.io.IOException;
import java.util.List;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2017. 9. 3.
 */
@Slf4j
public class ProfileConsumer {
    private final int MAX_PROFILE_BLOCK = 10;

    public List<Step> retrieveProfile(final ProfileRequest profileRequest) {
        MapPack param = new MapPack();
        param.put(ParamConstant.DATE, profileRequest.getYyyymmdd());
        param.put(ParamConstant.XLOG_TXID, profileRequest.getTxid());
        param.put(ParamConstant.PROFILE_MAX, MAX_PROFILE_BLOCK);

        XLogProfilePack pack;
        try (TcpProxy tcpProxy = TcpProxy.getTcpProxy(profileRequest.getServerId())) {
            pack = (XLogProfilePack)
                    tcpProxy.getSingle(RequestCmd.TRANX_PROFILE, param);
        } catch (IOException e) {
            throw ErrorState.INTERNAL_SERVER_ERRROR.newException(e.getMessage(), e);
        }

        return Step.toObjectList(pack.profile);
    }
}
