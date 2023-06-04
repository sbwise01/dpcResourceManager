package com.sbwise01.dpcresourcemanager;

import java.io.IOException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author bwise
 */
@Path("")
public class Main {

    @GET
    @Produces("application/json")
    @Path("createvbox")
    public Response getCreateVbox(
        @DefaultValue("") @QueryParam("machinename") String machineName,
        @DefaultValue("") @QueryParam("imagename") String imageName
    ) throws IOException, InterruptedException {
        Response response;

        if (machineName.isEmpty() || imageName.isEmpty()) {
            JSONObject output = new JSONObject();
            output.put("message", "Invalid request");
            output.put("stdout", "");
            output.put("stderr", "Parameters machinename and imagename must be provided");
            output.put("returnCode", "1");
            response = Response.status(Status.BAD_REQUEST).entity(output.toJSONString()).build();
        } else {
            JSONObject output = buildMachine(machineName, imageName);
            response = Response.status(Status.OK).entity(output.toJSONString()).build();
        }

        return response;
    }
    
    private JSONObject buildMachine(String machineName, String imageName) throws IOException, InterruptedException {
        // Setup
        ProcessOutput po;
        JSONObject response = new JSONObject();
        JSONArray stdoutJson = new JSONArray();
        JSONArray stderrJson = new JSONArray();
        JSONArray returnCodeJson = new JSONArray();
        String baseFolder = System.getenv("HOME") + "/VirtualBox VMs/";

        // Create VM
        String osType = ImageManager.getOsType(imageName);
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","createvm","--name",machineName,"--basefolder",baseFolder,"--ostype",osType,"--register");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        
        // Set memory and network
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","modifyvm",machineName,"--ioapic","on");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","modifyvm",machineName,"--memory","2048","--vram","128");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","modifyvm",machineName,"--nic1","bridged","--nictype1","82540EM","--bridgeadapter1","en0: Wi-Fi (AirPort)");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());

        // Create disk and connect prebuilt disk image
        String sourceImageFile = ImageManager.getImageName(imageName);
        String destImageFile = baseFolder + machineName + "/" + machineName + "_DISK.vdi";
        po = ProcessOutput.runCommand("cp",sourceImageFile,destImageFile);
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","storagectl",machineName,"--name","SATA Controller","--add","sata","--controller","IntelAHCI","--portcount","1","--bootable","on");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","internalcommands","sethduuid",destImageFile);
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","storageattach",machineName,"--storagectl","SATA Controller","--port","0","--device","0","--type","hdd","--medium",destImageFile);
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","modifyvm",machineName,"--boot1","disk","--boot2","none","--boot3","none","--boot4","none");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());

        // Start VM
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","startvm",machineName,"--type","headless");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());

        response.put("message", "Completed creation of machine " + machineName);
        response.put("stdout", stdoutJson);
        response.put("stderr", stderrJson);
        response.put("returnCode", returnCodeJson);
        return response;
    }

    @GET
    @Produces("application/json")
    @Path("removevbox")
    public Response getRemoveVbox(
        @DefaultValue("") @QueryParam("machinename") String machineName
    ) throws IOException, InterruptedException {
        Response response;

        if (machineName.isEmpty()) {
            JSONObject output = new JSONObject();
            output.put("message", "Invalid request");
            output.put("stdout", "");
            output.put("stderr", "Parameter machinename must be provided");
            output.put("returnCode", "1");
            response = Response.status(Status.BAD_REQUEST).entity(output.toJSONString()).build();
        } else {
            JSONObject output = removeMachine(machineName);
            response = Response.status(Status.OK).entity(output.toJSONString()).build();
        }

        return response;
    }
    
    private JSONObject removeMachine(String machineName) throws IOException, InterruptedException {
        // Setup
        ProcessOutput po;
        JSONObject response = new JSONObject();
        JSONArray stdoutJson = new JSONArray();
        JSONArray stderrJson = new JSONArray();
        JSONArray returnCodeJson = new JSONArray();
        String baseFolder = System.getenv("HOME") + "/VirtualBox VMs/";

        // Turn off VM
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","controlvm",machineName,"poweroff");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());
        po = ProcessOutput.runCommand("/usr/local/bin/VBoxManage","unregistervm",machineName,"--delete");
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());

        // Cleanup VM directory
        String machineDir = baseFolder + machineName;
        po = ProcessOutput.runCommand("rm","-rf",machineDir);
        stdoutJson.add(po.getStdout());
        stderrJson.add(po.getStderr());
        returnCodeJson.add(po.getReturnCode());

        response.put("message", "Completed remove of machine " + machineName);
        response.put("stdout", stdoutJson);
        response.put("stderr", stderrJson);
        response.put("returnCode", returnCodeJson);
        return response;
    }
}
