

function electronAPI(){
  return window.electronAPI
}

export async function getLocalServerAddress() {
  return await electronAPI().getLocalServerAddress()
}

export async function getLocalServerApi() {
  return await getLocalServerAddress() +"/api"
}
export async function getDataPath(){
  return  await electronAPI().getDataPath()
}
