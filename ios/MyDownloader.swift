//
//  MyDownloader.swift
//  rnapp
//
//  Created by  on 12/03/23.
//

import Foundation
import UIKit
import React

@objcMembers class MyDownloader: NSObject {
  
  func downloadFile(urlString: NSString, fileName: NSString, fileExtension: NSString) {
    let url = URL(string: urlString as String)
    let documentsUrl:URL =  (FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first as URL?)!
    let destinationFileUrl = documentsUrl.appendingPathComponent("\(fileName)\(UUID()).\(fileExtension)")
    let fileURL = URL(string: urlString as String)
    let sessionConfig = URLSessionConfiguration.default
    let session = URLSession(configuration: sessionConfig)
    let request = URLRequest(url:fileURL!)
    
    let task = session.downloadTask(with: request) { (tempLocalUrl, response, error) in
      if let tempLocalUrl = tempLocalUrl, error == nil {
        
        do {
          
          try FileManager.default.copyItem(at: tempLocalUrl, to: destinationFileUrl)
          
          do {
            //Show UIActivityViewController to save the downloaded file
            let contents  = try FileManager.default.contentsOfDirectory(at: documentsUrl, includingPropertiesForKeys: nil, options: .skipsHiddenFiles)
            
            for indexx in 0..<contents.count {
              if contents[indexx].lastPathComponent == destinationFileUrl.lastPathComponent {
                let activityViewController = UIActivityViewController(activityItems: [contents[indexx]], applicationActivities: nil)
                let presentedViewController = RCTPresentedViewController()
                DispatchQueue.main.async {
                  presentedViewController?.present(activityViewController, animated: true, completion: nil)
                }
              }
            }
          }
          catch (let err) {
            print("error: \(err)")
          }
        } catch (let writeError) {
          print("Error creating a file \(destinationFileUrl) : \(writeError)")
          
          
        }
      } else {
        print("Error took place while downloading a file. Error description: \(error?.localizedDescription ?? "")")
      }
    }
    task.resume()
    
  }
  
}
